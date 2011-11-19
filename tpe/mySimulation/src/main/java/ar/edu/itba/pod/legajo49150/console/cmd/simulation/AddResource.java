package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.agent.market.Resource;
import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class AddResource extends ContextCommand<NodeService> {

	public AddResource(NodeService node, Map<String, Object> context) {
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
