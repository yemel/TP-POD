package ar.edu.itba.pod.legajo49150.node;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.List;

import org.joda.time.Duration;

import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.node.api.ClusterAdministration;
import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.agent.runner.Simulation;
import ar.edu.itba.pod.legajo49150.simulation.DistributedSimulation;
import ar.edu.itba.pod.legajo49150.simulation.LocalDispatcher;
import ar.edu.itba.pod.legajo49150.simulation.Publisher;
import ar.edu.itba.pod.legajo49150.simulation.RemoteDispatcher;
import ar.edu.itba.pod.time.TimeMapper;

public class SimulationNode extends ClusterNode implements Simulation {

	protected Simulation sim;
	protected RemoteDispatcher dispatcher;
	
	// TODO: Abstraer
	public static SimulationNode newNode(String id, String host, int port, TimeMapper mapper) throws RemoteException, AlreadyBoundException {
		SimulationNode node = new SimulationNode(new NodeInformation(host, port, id), mapper);
		directory.publishAdmin(node, host, port);
		directory.publishDispatcher(node.dispatcher, host, port);
		return node;
	}
	
	public SimulationNode(NodeInformation nodeInfo, TimeMapper maper) throws RemoteException {
		super(nodeInfo);
		LocalDispatcher localDispatcher = new LocalDispatcher(this); 
		sim = new DistributedSimulation(maper, localDispatcher);
		dispatcher = new RemoteDispatcher(localDispatcher, this, directory);
		new Thread(new Publisher(this)).start();
	}

	@Override
	public void add(Agent agent) {
		sim.add(agent);
	}

	@Override
	public int agentsRunning() {
		return sim.agentsRunning();
	}

	@Override
	public Duration elapsed() {
		return sim.elapsed();
	}

	@Override
	public List<Agent> getAgentsRunning() {
		return sim.getAgentsRunning();
	}

	@Override
	public Duration remaining() {
		return sim.remaining();
	}

	@Override
	public void remove(Agent agent) {
		sim.remove(agent);
	}

	@Override
	public void start(Duration duration) {
		sim.start(duration);
	}

	@Override
	public void startAndWait(Duration duration) throws InterruptedException {
		sim.startAndWait(duration);
	}

	@Override
	public void stop() throws InterruptedException {
		sim.stop();
	}

	@Override
	protected void addNode(NodeInformation nodeInfo) {
		super.addNode(nodeInfo);
		dispatcher.registerNode(nodeInfo);
	}

	@Override
	protected void removeNode(NodeInformation nodeInfo) {
		super.removeNode(nodeInfo);
		dispatcher.unregisterNode(nodeInfo);
	}
	
	public ClusterAdministration getAdministration(){
		return this;
	}
	
	public RemoteDispatcher getDispatcher(){
		return dispatcher;
	}

	public NameResolver getNameResolver(){
		return directory;
	}
	
}
