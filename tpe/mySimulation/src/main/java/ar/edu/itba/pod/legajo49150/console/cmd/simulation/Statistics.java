package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.agent.market.AgentState;
import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;
import ar.edu.itba.pod.legajo49150.simulation.statistics.ClusterStatistics.Stats;

public class Statistics extends ContextCommand<NodeService> {

	public Statistics(NodeService node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(!(args.isEmpty() || args.get(0).equals("-v"))){
			System.out.println("Invalid arguments");
			return;
		}

		List<Stats> stats;
		try {
			stats = nodeService.getStatistics().getClusterStatistics();
			for(Stats node: stats){
				System.out.println("-> Node: " + node.node().id() + " (" + node.node() +")");
				System.out.println("   (+) #"+ node.stats().getNumberOfAgents() + " agents running:");
				if(args.size() != 0){
					for(AgentState each: node.stats().getAgentState())
						System.out.println("   --> " + each);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getDescription() {
		return "Informa el estado general del cluster";
	}

	@Override
	protected String getName() {
		return "stats";
	}

}
