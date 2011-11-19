package ar.edu.itba.pod.legajo49150.console.cmd.other;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.legajo49150.console.ContextCommand;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class Coordinator extends ContextCommand<NodeService> {
	
	public Coordinator(NodeService node, Map<String, Object> context) {
		super(node, context);
	}

	@Override
	protected void execute(List<String> args) {
		if(args.size() != 0){
			System.out.println("Invalid arguments");
			return;
		}
		
		try {
			System.out.println("The coordinator is: " + nodeService.getBalancer().getCoordinator());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getDescription() {
		return "Display the current coordinator";
	}

	@Override
	protected String getName() {
		return "coordinator";
	}


}
