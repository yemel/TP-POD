package ar.edu.itba.pod.legajo49150.console.cmd.simulation;

import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.agent.market.Resource;
import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class ListResources extends ContextCommand<NodeService> {

	public ListResources(NodeService node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(!args.isEmpty()){
			System.out.println("Invalid arguments");
			return;
		}
		
		for(Resource each: resources().values()){
			System.out.println("\t -> " + each.toString());
		}
	}

	@Override
	protected String getDescription() {
		return "Lista los recursos cargados";
	}

	@Override
	protected String getName() {
		return "resources";
	}

}
