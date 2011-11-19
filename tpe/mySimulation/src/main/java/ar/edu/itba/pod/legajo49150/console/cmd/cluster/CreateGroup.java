package ar.edu.itba.pod.legajo49150.console.cmd.cluster;

import java.rmi.RemoteException;
import java.util.List;

import ar.edu.itba.pod.legajo49150.console.Command;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class CreateGroup extends Command<NodeService> {

	public CreateGroup(NodeService node){
		super(node);
	}
	
	@Override
	protected void execute(List<String> args) {
		try {
			nodeService.getAdministrator().createGroup();
			nodeService.getBalancer().getCoordinator();
		} catch (RemoteException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	@Override
	protected String getDescription() {
		return "Create a new cluster group";
	}

	@Override
	protected String getName() {
		return "create";
	}

}
