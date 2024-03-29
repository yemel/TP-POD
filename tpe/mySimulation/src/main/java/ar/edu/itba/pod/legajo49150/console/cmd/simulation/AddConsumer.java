package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import org.joda.time.Duration;

import ar.edu.itba.pod.agent.market.Consumer;
import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class AddConsumer extends ContextCommand<NodeService> {

	public AddConsumer(NodeService node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(args.size() != 4){
			System.out.println("Invalid arguments");
			return;
		}

		Duration d = Duration.standardHours(Integer.valueOf(args.get(2)));
		String name = args.get(0);
		Consumer c = new Consumer(name, resources().get(args.get(1)), d, 1);
		nodeService.getBalancer().addAgentToCluster(c);
	}

	@Override
	protected String getDescription() {
		return "Agrega un consumidor <Name> <ResourseName> <Days> <Amount>";
	}

	@Override
	protected String getName() {
		return "addC";
	}

}
