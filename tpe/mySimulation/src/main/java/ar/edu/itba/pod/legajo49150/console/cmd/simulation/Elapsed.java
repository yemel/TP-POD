package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class Elapsed extends ContextCommand<NodeService> {

	public Elapsed(NodeService node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(!args.isEmpty()){
			System.out.println("Invalid arguments");
			return;
		}
		
		nodeService.getSimulation().elapsed();
	}

	@Override
	protected String getDescription() {
		return "Informa el tiempo transcurrido";
	}

	@Override
	protected String getName() {
		return "elapsed";
	}

}
