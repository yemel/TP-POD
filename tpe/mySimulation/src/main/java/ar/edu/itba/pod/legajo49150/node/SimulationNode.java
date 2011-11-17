package ar.edu.itba.pod.legajo49150.node;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import org.joda.time.Duration;

import ar.edu.itba.balance.api.AgentsTransfer;
import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.node.api.ClusterAdministration;
import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.agent.runner.Simulation;
import ar.edu.itba.pod.legajo49150.simulation.DistributedSimulation;
import ar.edu.itba.pod.legajo49150.simulation.LocalDispatcher;
import ar.edu.itba.pod.legajo49150.simulation.balance.ClusterBalancer;
import ar.edu.itba.pod.legajo49150.simulation.events.RemoteDispatcher;
import ar.edu.itba.pod.time.TimeMapper;

public class SimulationNode extends ClusterNode implements Simulation {

	protected Simulation sim;
//	protected RemoteDispatcher dispatcher;
//	protected ClusterBalancer balancer;
	
	public SimulationNode(NodeInformation nodeInfo, TimeMapper maper) throws RemoteException {
		super(nodeInfo);
		sim = new DistributedSimulation(maper, localDispatcher);
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
		dispatcher.start();
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
	}

	@Override
	protected void removeNode(NodeInformation nodeInfo) {
		super.removeNode(nodeInfo);
	}

	@Override
	public synchronized void createGroup() throws RemoteException {
		super.createGroup();
		balancer.getCoordinator();
	}

	@Override
	public void connectToGroup(String host, int port) throws RemoteException,
			NotBoundException {
		super.connectToGroup(host, port);
		balancer.getCoordinator();
	}
	
	
}
