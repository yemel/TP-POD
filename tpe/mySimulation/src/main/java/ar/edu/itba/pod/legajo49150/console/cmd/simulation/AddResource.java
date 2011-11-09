package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.agent.market.Resource;
import ar.edu.itba.pod.legajo49150.console.SimulationCommand;
import ar.edu.itba.pod.legajo49150.node.SimulationNode;

public class AddResource extends SimulationCommand<SimulationNode> {

	public AddResource(SimulationNode node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(args.size() != 2){
			System.out.println("Invalid arguments");
			return;
		}
		
		Resource r = new Resource(args.get(0), args.get(1));
		resources().put(args.get(1), r);
	}

	@Override
	protected String getDescription() {
		return "Agrega un recurso <Category> <Name>";
	}

	@Override
	protected String getName() {
		return "addR";
	}

}
