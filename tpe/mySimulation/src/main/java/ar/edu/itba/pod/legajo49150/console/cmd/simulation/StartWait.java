package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import org.joda.time.Duration;

import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class StartWait extends ContextCommand<NodeService> {

	public StartWait(NodeService node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(args.size() != 1){
			System.out.println("Invalid arguments");
			return;
		}
		
		Duration d = Duration.standardMinutes(Integer.valueOf(args.get(0)));
		nodeService.getDispatcher().start();
		nodeService.getSimulation().start(d);
	}

	@Override
	protected String getDescription() {
		return "Inicia la simulación y espera <Minutes>";
	}

	@Override
	protected String getName() {
		return "startWait";
	}

}
