package ar.edu.itba.pod.legajo49150.console.cmd.cluster;

import java.util.List;

import ar.edu.itba.pod.legajo49150.console.Command;
import ar.edu.itba.pod.legajo49150.node.ClusterNode;

public class Disconnect extends Command<ClusterNode> {

	public Disconnect(ClusterNode node){
		super(node);
	}
	
	@Override
	protected void execute(List<String> args) {
		try {
			node.disconnectFromGroup(node.getNodeInfo());
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
