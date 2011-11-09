package ar.edu.itba.pod.legajo49150.console;

import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.legajo49150.console.cmd.cluster.Connect;
import ar.edu.itba.pod.legajo49150.console.cmd.cluster.ConnectedNodes;
import ar.edu.itba.pod.legajo49150.console.cmd.cluster.CreateGroup;
import ar.edu.itba.pod.legajo49150.console.cmd.cluster.Disconnect;
import ar.edu.itba.pod.legajo49150.node.ClusterNode;
import ar.edu.itba.pod.legajo49150.node.SimulationNode;

public class ClusterConsole<T extends ClusterNode> extends AbstractConsole<T> {

	@SuppressWarnings("unchecked")
	public ClusterConsole(SimulationNode node) {
		super(System.in, (T)node);
	}

	@Override
	protected void loadCommands(List<Command<?>> commands, T node, Map<String, Object> context) {
		// TODO: Si en el futuro tengo problemas con la herencia,
		// hacer que simulationConsole deje de heredar de esto
		commands.add(new CreateGroup(node));
		commands.add(new Connect(node));
		commands.add(new Disconnect(node));
		commands.add(new ConnectedNodes(node));		
		
	}

	@Override
	protected void loadContext(Map<String, Object> context) {
	}
	
}
