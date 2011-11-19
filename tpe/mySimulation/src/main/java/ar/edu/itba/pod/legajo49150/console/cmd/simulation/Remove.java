package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class Remove extends ContextCommand<NodeService> {

	public Remove(NodeService node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(args.size() != 1){
			System.out.println("Invalid arguments");
			return;
		}
		
		Agent agent = agents().get(args.get(0));
		nodeService.getSimulation().remove(agent);
		agents().remove(args.get(0));
	}

	@Override
	protected String getDescription() {
		return "Elimina un agente <AgentName>";
	}

	@Override
	protected String getName() {
		return "remove";
	}

}
