package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.legajo49150.console.SimulationCommand;
import ar.edu.itba.pod.legajo49150.node.SimulationNode;

public class Remaining extends SimulationCommand<SimulationNode> {

	public Remaining(SimulationNode node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(!args.isEmpty()){
			System.out.println("Invalid arguments");
			return;
		}
		
		node.remaining();
	}

	@Override
	protected String getDescription() {
		return "Informa el tiempo restante";
	}

	@Override
	protected String getName() {
		return "remaining";
	}

}
