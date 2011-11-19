package ar.edu.itba.pod.legajo49150.simulation.balance;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import ar.edu.itba.balance.api.AgentsBalancer;
import ar.edu.itba.balance.api.AgentsTransfer;
import ar.edu.itba.balance.api.NodeAgent;
import ar.edu.itba.balance.api.NotCoordinatorException;
import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.doc.ThreadSafe;
import ar.edu.itba.pod.legajo49150.node.NodeService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@ThreadSafe
public class ClusterBalancer implements AgentsBalancer {
	private final Logger LOGGER = Logger.getLogger(ClusterBalancer.class);

	private final NodeService services;
	private final Set<Election> elections = Collections.newSetFromMap(new ConcurrentHashMap<Election, Boolean>());
	private final BlockingQueue<Election> toProcess = new LinkedBlockingQueue<Election>();

	private volatile CountDownLatch bullyLatch = new CountDownLatch(0);

	private final Thread publisherThread = new Thread(new ElectionPublisher());
	private final AtomicBoolean threadStop = new AtomicBoolean(false);

	private final AtomicReference<NodeInformation> currentCoordinator = new AtomicReference<NodeInformation>(null);

	Lock lock = new ReentrantLock();
	Condition isDone = lock.newCondition();

	public ClusterBalancer(NodeService services) throws RemoteException{
		this.services = services;
		UnicastRemoteObject.exportObject(this, 0);
	}

	@Override
	public void bullyElection(NodeInformation node, long timestamp)
			throws RemoteException {
		Election e = new Election(node, timestamp, Election.MsgType.ELECT);
		if(!elections.contains(e)){
			elections.add(e);
			toProcess.add(e);
		}
	}

	@Override
	public void bullyOk(NodeInformation node) throws RemoteException {
		bullyLatch.countDown();
	}

	@Override
	public void bullyCoordinator(NodeInformation node, long timestamp)
			throws RemoteException {
		Election e = new Election(node, timestamp, Election.MsgType.CORD);
		if(!elections.contains(e)){
			elections.add(e);
			toProcess.add(e);
		}
	}

	public void localShutdown() throws RemoteException, NotCoordinatorException{
		List<NodeAgent> agents = services.getTransfer().stopAndGet(services.getSimulation().agentsRunning());
		AgentsBalancer balancer = services.getDirectory().getBalancer(getCoordinator());
		balancer.shutdown(agents);
	}
	
	@Override
	public void shutdown(List<NodeAgent> agents) throws RemoteException,
	NotCoordinatorException {
		if(agents.size() != 0){
			Set<NodeInformation> nodes = services.getAdministrator().connectedNodes();
			nodes.remove(agents.get(0).node());
			rebalance(agents, nodes);
		}
	}

	@Override
	public void addAgentToCluster(NodeAgent agent) throws RemoteException,
	NotCoordinatorException {
		List<NodeAgent> agents = Lists.newArrayList();
		agents.add(agent);
		Set<NodeInformation> nodes = services.getAdministrator().connectedNodes();
		rebalance(agents, nodes);
	}

	public void rebalance() throws RemoteException, NotCoordinatorException{
		List<NodeAgent> agents = Lists.newArrayList();
		Set<NodeInformation> nodes = services.getAdministrator().connectedNodes();
		rebalance(agents, nodes);
	}
	
	private void rebalance(List<NodeAgent> agents, Set<NodeInformation> remaining) throws NotCoordinatorException, RemoteException{
		if(!isCoordinator()){
			throw new NotCoordinatorException(currentCoordinator.get());
		}

		Map<NodeInformation,Integer> cluster = Maps.newHashMap();
		int totalAgents = agents.size();

		// Map: <NodosVivimos,CantAgent>
		for(NodeInformation node: remaining){
			AgentsTransfer transfer = services.getDirectory().getTransfer(node);
			int nagents = transfer.getNumberOfAgents();
			cluster.put(node, nagents);
			totalAgents += nagents;
		}
		
		// Sort by Value
		List<Map.Entry<NodeInformation, Integer>> entries = Lists.newLinkedList(cluster.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<NodeInformation, Integer>>() {
			@Override
			public int compare(Entry<NodeInformation, Integer> o1,
					Entry<NodeInformation, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		
		// Reacomodamos los nodos
		int maxAgent = (int) Math.ceil((double)totalAgents/(double)remaining.size());
		for(Entry<NodeInformation, Integer> each: entries){
			int toTransfer = Math.min(maxAgent - each.getValue(), agents.size());
			if(toTransfer > 0){
				AgentsTransfer transfer = services.getDirectory().getTransfer(each.getKey());
				transfer.runAgentsOnNode(Lists.newArrayList(agents.subList(0, toTransfer)));
				agents = agents.subList(toTransfer, agents.size());
			} else if(toTransfer < 0){
				toTransfer = -toTransfer;
				AgentsTransfer transfer = services.getDirectory().getTransfer(each.getKey());
				List<NodeAgent> extras = transfer.stopAndGet(toTransfer);
				agents.addAll(extras);
			}
		}
		
		LOGGER.debug("TERMINAMOS DE REBALANCEAR, QUEDARON " + agents.size() + " AGENTES COLGADOS!");
	}

	public void addAgentToCluster(Agent agent){
		try {
			AgentsBalancer balancer = services.getDirectory().getBalancer(getCoordinator());
			NodeAgent a = new NodeAgent(null, agent);
			balancer.addAgentToCluster(a);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isCoordinator(){
		return services.getAdministrator().getNodeInfo().equals(currentCoordinator.get());
	}

	public NodeInformation getCoordinator() throws RemoteException {
		if(currentCoordinator.get() == null){
			bullyElection(services.getAdministrator().getNodeInfo(), DateTime.now().getMillis());
			try {
				lock.lock();
				isDone.await(5000, TimeUnit.MILLISECONDS);
				lock.unlock();
			} catch (InterruptedException e) { LOGGER.error("Interrupted while waiting coordinator election");	}
		}
		return currentCoordinator.get();
	}

	public void start(){
		this.publisherThread.start();
	}

	public void stop(){
		this.threadStop.set(true);
		this.publisherThread.interrupt();
	}

	private class ElectionPublisher implements Runnable {

		@Override
		public void run() {
			NodeInformation self = services.getAdministrator().getNodeInfo();
			while(!threadStop.get()){
				try {
					Election e = toProcess.take();
					if(e.isElection()){
						processElection(self, e);
					} else {
						processCoordinator(e);
					}

					boolean timeOut = bullyLatch.await(2000, TimeUnit.MILLISECONDS);
					if(!timeOut){
						bullyCoordinator(self, DateTime.now().getMillis());
						bullyLatch.countDown();
					}
				} catch (Exception e2) { LOGGER.error("ThreadCoordinator", e2); return; }
			}
		}

		private void processElection(NodeInformation self, Election e) throws RemoteException{
			if(self.equals(e.getNode())){
				bullyLatch = new CountDownLatch(1); // me propongo cordinador
			}

			if(self.id().compareTo(e.getNode().id()) > 0){
				services.getDirectory().getBalancer(e.getNode()).bullyOk(self);
				bullyElection(self, new DateTime().getMillis());
				return; // No broadcasteamos el evento porque soy más grande!
			}

			Set<NodeInformation> nodes = services.getAdministrator().connectedNodes();
			for(NodeInformation each: nodes){
				if(!each.equals(services.getAdministrator().getNodeInfo())){
					AgentsBalancer balancer = services.getDirectory().getBalancer(each);
					balancer.bullyElection(e.getNode(), e.getTimestamp());
				}
			}
		}

		private void processCoordinator(Election e) throws RemoteException{
			currentCoordinator.set(e.getNode());
			for(NodeInformation each: services.getAdministrator().connectedNodes()){
				if(!each.equals(services.getAdministrator().getNodeInfo())){
					AgentsBalancer balancer = services.getDirectory().getBalancer(each);
					balancer.bullyCoordinator(e.getNode(), e.getTimestamp());
				}
			}

			lock.lock();
			isDone.signalAll();
			lock.unlock();
		}

	}

}
