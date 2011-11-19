package ar.edu.itba.pod.legajo49150.console.cmd.cluster;

import java.util.List;

import ar.edu.itba.pod.legajo49150.console.Command;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class Connect extends Command<NodeService> {

	public Connect(NodeService node) {
		super(node);
	}

	@Override
	protected void execute(List<String> args) {
		try {
			nodeService.getAdministrator().connectToGroup(args.get(0), Integer.valueOf(args.get(1)));
			nodeService.getBalancer().getCoordinator();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error: " + e.getMessage());
		}
	}

	@Override
	protected String getDescription() {
		return "Connect to a cluster <Host> <Port>";
	}

	@Override
	protected String getName() {
		return "connect";
	}

}
