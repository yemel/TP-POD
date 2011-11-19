package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class Stop extends ContextCommand<NodeService> {

	public Stop(NodeService node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(!args.isEmpty()){
			System.out.println("Invalid arguments");
			return;
		}
		
		try {
			nodeService.getSimulation().stop();
			nodeService.getDispatcher().stop();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getDescription() {
		return "Detiene la simulación";
	}

	@Override
	protected String getName() {
		return "stop";
	}

}
