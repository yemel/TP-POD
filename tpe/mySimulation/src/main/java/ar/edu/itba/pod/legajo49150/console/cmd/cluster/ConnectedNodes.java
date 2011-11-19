package ar.edu.itba.pod.legajo49150.console.cmd.cluster;

import java.util.List;
import java.util.Set;

import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.pod.legajo49150.console.Command;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class ConnectedNodes extends Command<NodeService> {

	public ConnectedNodes(NodeService node){
		super(node);
	}
	
	@Override
	protected void execute(List<String> args) {
		try {
			Set<NodeInformation> nodes = nodeService.getAdministrator().connectedNodes();
			System.out.print("Nodes: [ ");
			for(NodeInformation each: nodes){
				System.out.print(each + " ");
			}
			System.out.println("]");
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	@Override
	protected String getDescription() {
		return "List the connected nodes";
	}

	@Override
	protected String getName() {
		return "nodes";
	}

}
