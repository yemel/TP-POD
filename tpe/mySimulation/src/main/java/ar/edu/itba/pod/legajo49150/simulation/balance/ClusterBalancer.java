package ar.edu.itba.pod.legajo49150.simulation.balance;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
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
import ar.edu.itba.pod.legajo49150.node.NodeService;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ClusterBalancer implements AgentsBalancer {
	private final Logger LOGGER = Logger.getLogger(ClusterBalancer.class);

	private final NodeService services;
	private final Set<Election> elections = Collections.newSetFromMap(new ConcurrentHashMap<Election, Boolean>());
	private final BlockingQueue<Election> toProcess = new LinkedBlockingQueue<Election>();

	private CountDownLatch bullyLatch = new CountDownLatch(0);

	private final Thread publisherThread = new Thread(new ElectionPublisher());
	private final AtomicBoolean threadStop = new AtomicBoolean(false);

	private AtomicReference<NodeInformation> currentCoordinator = new AtomicReference<NodeInformation>(null);
	
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
			toProcess.add(e);
			LOGGER.debug("Election message from " + node + " @" + timestamp);
		}
	}

	@Override
	public void bullyOk(NodeInformation node) throws RemoteException {
		LOGGER.debug("OK recived, i'm not the coordinator. May " + node + " is");
		bullyLatch.countDown();
	}

	@Override
	public void bullyCoordinator(NodeInformation node, long timestamp)
			throws RemoteException {
		Election e = new Election(node, timestamp, Election.MsgType.CORD);
		if(!elections.contains(e)){
			toProcess.add(e);
			LOGGER.debug("The Coordinator is " + node + " @" + timestamp);
		}
	}

	@Override
	public void shutdown(List<NodeAgent> agents) throws RemoteException,
	NotCoordinatorException {
		if(!isCoordinator()){
			throw new NotCoordinatorException(currentCoordinator.get());
		}

		if(agents.size() == 0){
			return;
		}

		Map<NodeInformation,Integer> cluster = Maps.newHashMap();
		int totalAgents = agents.size();
		int remainingNodes = 0;

		// Map: <NodosVivimos,CantAgent>, Lista: Agentes a mover, Nodos totales
		for(NodeInformation node: services.getAdministrator().connectedNodes()){
			if(!node.equals(agents.get(0).node())){
				AgentsTransfer transfer = services.getDirectory().getTransfer(node);
				int nagents = transfer.getNumberOfAgents();
				cluster.put(node, nagents);
				totalAgents += nagents;
				remainingNodes++;
			}
		}

		// Cada Agente recibe TECHO(TOTALES/QUEDAN - TIENE) y salen de la lista.
		int maxAgent = (int) Math.ceil((double)totalAgents/(double)remainingNodes);
		for(Entry<NodeInformation, Integer> each: cluster.entrySet()){
			int toTransfer = Math.min(maxAgent - each.getValue(), agents.size());
			if(toTransfer > 0){
				AgentsTransfer transfer = services.getDirectory().getTransfer(each.getKey());
				transfer.runAgentsOnNode(agents.subList(0, toTransfer));
			}
		}
	}

	@Override
	public void addAgentToCluster(NodeAgent agent) throws RemoteException,
	NotCoordinatorException {
		if(!isCoordinator()){
			throw new NotCoordinatorException(currentCoordinator.get());
		}
		Set<NodeInformation> connectedNodes = services.getAdministrator().connectedNodes();
		Map<NodeInformation,Integer> cluster = Maps.newHashMap();
		int totalAgents = 1;
		int remainingNodes = connectedNodes.size();

		// Map: <NodosVivimos,CantAgent>, Lista: Agentes a mover, Nodos totales
		for(NodeInformation node: connectedNodes){
			AgentsTransfer transfer = services.getDirectory().getTransfer(node);
			int nagents = transfer.getNumberOfAgents();
			cluster.put(node, nagents);
			totalAgents += nagents;
		}
		
		int maxAgent = (int) Math.ceil((double)totalAgents/(double)remainingNodes);
		for(Entry<NodeInformation, Integer> each: cluster.entrySet()){
			int toTransfer = maxAgent - each.getValue();
			if(toTransfer > 0){
				AgentsTransfer transfer = services.getDirectory().getTransfer(each.getKey());
				List<NodeAgent> a = Lists.newArrayList();
				a.add(agent);
				transfer.runAgentsOnNode(a);
			}
		}
		
	}

	private boolean isCoordinator(){
		return services.getAdministrator().getNodeInfo().equals(currentCoordinator.get());
	}

	public NodeInformation getCoordinator() throws RemoteException {
		LOGGER.debug("GetCoordinator");
		if(currentCoordinator.get() == null){
			bullyElection(services.getAdministrator().getNodeInfo(), DateTime.now().getMillis());
		}
		try {
			LOGGER.debug("Waiting at the lock");
			lock.lock();
			isDone.await(5000, TimeUnit.MILLISECONDS);
			lock.unlock();
		} catch (InterruptedException e) { LOGGER.error("Interrupted while waiting coordinator election");	}
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
					LOGGER.debug("Esperando para procesar");
					Election e = toProcess.take();
					if(e.isElection()){
						processElection(self, e);
					} else {
						processCoordinator(e);
					}

					try {
						LOGGER.debug("Espero respuesta del resto");
						bullyLatch.await(2000, TimeUnit.MILLISECONDS);
						LOGGER.debug("Pasé de largo");
					} catch (InterruptedException e1) {
						LOGGER.debug("SIN RESPUESTAS!");
						bullyCoordinator(self, DateTime.now().getMillis());
					}
				} catch (Exception e2) { return; }
			}
		}

		private void processElection(NodeInformation self, Election e) throws RemoteException{
			LOGGER.debug("Procesando elección!");
			if(self.equals(e.getNode())){
				bullyLatch = new CountDownLatch(1); // me propongo cordinador
				LOGGER.debug("Me propongo coordinador!");
			}

			if(self.id().compareTo(e.getNode().id()) > 0){
				services.getDirectory().getBalancer(e.getNode()).bullyOk(self);
				bullyElection(self, new DateTime().getMillis());
				return; // No broadcasteamos el evento porque soy más grande!
			}

			// Broadcast de la election para mayores y la mia
			Set<NodeInformation> nodes = services.getAdministrator().connectedNodes();
			for(NodeInformation each: nodes){
				LOGGER.debug("Broadcast");
				AgentsBalancer balancer = services.getDirectory().getBalancer(each);
				balancer.bullyElection(e.getNode(), e.getTimestamp());
			}
		}

		private void processCoordinator(Election e) throws RemoteException{
			currentCoordinator.set(e.getNode());
			for(NodeInformation each: services.getAdministrator().connectedNodes()){
				AgentsBalancer balancer = services.getDirectory().getBalancer(each);
				balancer.bullyCoordinator(e.getNode(), e.getTimestamp());
			}
			
			lock.lock();
			isDone.signalAll();
			lock.unlock();
		}

	}

}
