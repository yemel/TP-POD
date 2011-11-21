package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;

import ar.edu.itba.pod.agent.market.Factory;
import ar.edu.itba.pod.agent.market.Resource;
import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;

public class AddFactory extends ContextCommand<NodeService> {

	public AddFactory(NodeService node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(args.size() != 5){
			System.out.println("Invalid arguments");
			return;
		}
		
		String name = args.get(0);
		Resource resource = resources().get(args.get(1));
		Iterable<Resource> req = Iterables.transform(Arrays.asList(args.get(2).split("-")), new Function<String, Resource>() {
			@Override
			public Resource apply(String from) {
				return resources().get(from);
			}
		});
		Iterable<Resource> req2 = Iterables.filter(req, Predicates.notNull());
		Duration d = Duration.standardHours(Integer.valueOf(args.get(3)));
		int amount = Integer.valueOf(args.get(4));
		Factory f = new Factory(name, resource, HashMultiset.create(req2), d, amount);
		nodeService.getBalancer().addAgentToCluster(f);
	}

	@Override
	protected String getDescription() {
		return "Agrega una factory <Name> <ResourseName> [Requirements] <Rate> <Amount>";
	}

	@Override
	protected String getName() {
		return "addF";
	}

}
