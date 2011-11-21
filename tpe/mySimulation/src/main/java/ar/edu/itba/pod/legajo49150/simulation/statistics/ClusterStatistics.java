package ar.edu.itba.pod.legajo49150.simulation.statistics;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.node.api.NodeStatistics;
import ar.edu.itba.node.api.StatisticReports;
import ar.edu.itba.pod.agent.market.AgentState;
import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.doc.ThreadSafe;
import ar.edu.itba.pod.legajo49150.node.NodeService;
import ar.edu.itba.pod.legajo49150.simulation.DistributedSimulation;

import com.google.common.collect.Lists;

@ThreadSafe
public class ClusterStatistics implements StatisticReports {

	private final DistributedSimulation simulation;
	private final NodeService services;
	
	public ClusterStatistics(NodeService services) throws RemoteException {
		this.simulation = services.getSimulation();
		this.services = services;
		UnicastRemoteObject.exportObject(this, 0);
	}
	
	@Override
	public NodeStatistics getNodeStatistics() throws RemoteException {
		int numberOfAgents = simulation.agentsRunning();
		List<AgentState> states = Lists.newArrayList();
		for(Agent agent: simulation.getAgentsRunning()){
			states.add(agent.state());
		}
		return new NodeStatistics(numberOfAgents, states);
	}

	public List<Stats> getClusterStatistics() throws RemoteException, NotBoundException{
		List<Stats> stats = Lists.newArrayList();
		for(NodeInformation node: services.getAdministrator().connectedNodes()){
			try {
			StatisticReports remote = services.getDirectory().getStatistics(node);
			stats.add(new Stats(node, remote.getNodeStatistics()));
			} catch (Exception e) {
				services.getAdministrator().disconnectNode(node);
			}
		}
		return stats;
	}
	
	public static class Stats {
		private final NodeInformation node;
		private final NodeStatistics stats;
		public Stats(NodeInformation node, NodeStatistics stats){
			this.node = node;
			this.stats = stats;
		}
		public NodeInformation node(){return node;}
		public NodeStatistics stats(){return stats;}
	}
	
}
