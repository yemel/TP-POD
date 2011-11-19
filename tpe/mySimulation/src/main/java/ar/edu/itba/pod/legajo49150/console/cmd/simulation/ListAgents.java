package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class ListAgents extends ContextCommand<NodeService> {

	public ListAgents(NodeService node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(!args.isEmpty()){
			System.out.println("Invalid arguments");
			return;
		}
		
		System.out.println("#" + nodeService.getSimulation().agentsRunning() + " agents running" );
		for(Agent each: nodeService.getSimulation().getAgentsRunning()){
			System.out.println("\t -> " + each.toString());
		}
	}

	@Override
	protected String getDescription() {
		return "Informa el tiempo por restantes";
	}

	@Override
	protected String getName() {
		return "agents";
	}

}
