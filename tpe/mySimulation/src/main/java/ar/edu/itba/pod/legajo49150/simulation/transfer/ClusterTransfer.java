package ar.edu.itba.pod.legajo49150.simulation.transfer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import ar.edu.itba.balance.api.AgentsTransfer;
import ar.edu.itba.balance.api.NodeAgent;
import ar.edu.itba.event.RemoteEventDispatcher;
import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.legajo49150.node.Directory;
import ar.edu.itba.pod.legajo49150.node.NodeService;

import com.google.common.collect.Lists;

public class ClusterTransfer implements AgentsTransfer {

	private final NodeService services;
	
	public ClusterTransfer(NodeService services) throws RemoteException {
		this.services = services;
		UnicastRemoteObject.exportObject(this, 0);
	}
	
	@Override
	public void runAgentsOnNode(List<NodeAgent> agents) throws RemoteException {
		Directory dir = services.getDirectory();
		for(NodeAgent nodeAgent: agents){
			if(nodeAgent.node() == null){
				services.getSimulation().add(nodeAgent.agent());
			} else {
				try {
					RemoteEventDispatcher dispatcher = dir.getDispatcher(nodeAgent.node());
					// Zona de exclusión mutua
					// Pull(B)
					// Flush(B)
					BlockingQueue<Object> events = dispatcher.moveQueueFor(nodeAgent.agent());
					services.getDispatcher().setQueueFor(nodeAgent.agent(), events);
					// Termina la zona de exclusión
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int getNumberOfAgents() throws RemoteException {
		return services.getSimulation().agentsRunning();
	}

	@Override
	public List<NodeAgent> stopAndGet(int numberOfAgents)
			throws RemoteException {
		List<Agent> agents = services.getSimulation().getAgentsRunning();
		agents.subList(0, Math.min(numberOfAgents, agents.size()));
		List<NodeAgent> ret = Lists.newArrayList();
		for(Agent agent: agents){
			services.getSimulation().remove(agent);
			ret.add(new NodeAgent(services.getAdministrator().getNodeInfo(), agent));
		}
		return ret;
	}

}
