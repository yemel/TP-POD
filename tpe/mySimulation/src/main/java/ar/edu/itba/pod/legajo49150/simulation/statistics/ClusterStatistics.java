package ar.edu.itba.pod.legajo49150.simulation.statistics;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import ar.edu.itba.node.api.NodeStatistics;
import ar.edu.itba.node.api.StatisticReports;
import ar.edu.itba.pod.agent.market.AgentState;
import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.legajo49150.node.SimulationNode;

import com.google.common.collect.Lists;

public class ClusterStatistics implements StatisticReports {

	private SimulationNode simNode = null; // TODO: Implementar 
	
	public ClusterStatistics(SimulationNode simNode) throws RemoteException {
		this.simNode = simNode;
		UnicastRemoteObject.exportObject(this, 0);
	}
	
	@Override
	public NodeStatistics getNodeStatistics() throws RemoteException {
		int numberOfAgents = simNode.agentsRunning();
		List<AgentState> states = Lists.newArrayList();
		for(Agent agent: simNode.getAgentsRunning()){
			states.add(agent.state());
		}
		return new NodeStatistics(numberOfAgents, states);
	}

}
