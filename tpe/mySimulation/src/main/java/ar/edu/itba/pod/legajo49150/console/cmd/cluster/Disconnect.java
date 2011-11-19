package ar.edu.itba.pod.legajo49150.console.cmd.cluster;

import java.util.List;

import ar.edu.itba.pod.legajo49150.console.Command;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class Disconnect extends Command<NodeService> {

	public Disconnect(NodeService node){
		super(node);
	}
	
	@Override
	protected void execute(List<String> args) {
		try {
			nodeService.getBalancer().localShutdown();
			System.out.println("Me estoy llendo con " + nodeService.getSimulation().agentsRunning() + " agentes");
			nodeService.getAdministrator().disconnectFromGroup(nodeService.getAdministrator().getNodeInfo());
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	@Override
	protected String getDescription() {
		return "Disconnect from de cluster";
	}

	@Override
	protected String getName() {
		return "disconnect";
	}

}
