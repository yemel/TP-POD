package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.agent.market.Market;
import ar.edu.itba.pod.legajo49150.console.SimulationCommand;
import ar.edu.itba.pod.legajo49150.node.SimulationNode;

public class AddMarket extends SimulationCommand<SimulationNode> {

	public AddMarket(SimulationNode node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(args.size() != 2){
			System.out.println("Invalid arguments");
			return;
		}
		
		String name = args.get(0);
		Market m = new Market(name, resources().get(args.get(1)));
		node.add(m);
		agents().put(name, m);
	}

	@Override
	protected String getDescription() {
		return "Agrega un productor <Name> <ResourseName>";
	}

	@Override
	protected String getName() {
		return "addM";
	}

}
