package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import org.joda.time.Duration;

import ar.edu.itba.pod.agent.market.Producer;
import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class AddProducer extends ContextCommand<NodeService> {

	public AddProducer(NodeService node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(args.size() != 4){
			System.out.println("Invalid arguments");
			return;
		}
		
		String name = args.get(0);
		Duration d = Duration.standardHours(Integer.valueOf(args.get(2)));
		int amount = Integer.valueOf(args.get(3));
		Producer p = new Producer(name, resources().get(args.get(1)), d, amount);
		nodeService.getBalancer().addAgentToCluster(p);
	}

	@Override
	protected String getDescription() {
		return "Agrega un productor <Name> <ResourseName> <Days> <Amount>";
	}

	@Override
	protected String getName() {
		return "addP";
	}

}
