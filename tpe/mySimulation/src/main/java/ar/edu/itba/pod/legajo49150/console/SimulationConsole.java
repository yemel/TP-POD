package ar.edu.itba.pod.legajo49150.console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.agent.market.Resource;
import ar.edu.itba.pod.agent.market.ResourceAgent;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.AddConsumer;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.AddMarket;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.AddProducer;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.AddResource;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.Elapsed;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.ListAgents;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.ListResources;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.Remaining;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.Remove;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.Start;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.StartWait;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.Stop;
import ar.edu.itba.pod.legajo49150.node.SimulationNode;

public class SimulationConsole<T extends SimulationNode> extends ClusterConsole<T> {

	public SimulationConsole(SimulationNode node) {
		super(node);
	}
	
	@Override
	protected void loadContext(Map<String, Object> context) {
		Map<String, Resource> resources = new HashMap<String, Resource>();
		Map<String, ResourceAgent> agents = new HashMap<String, ResourceAgent>();
		context.put(SimulationCommand.RESOURCES, resources);
		context.put(SimulationCommand.AGENTS, agents);
		loadResources();
	}

	@Override
	protected void loadCommands(List<Command<?>> commands, T node, Map<String, Object> context) {
		super.loadCommands(commands, node, context);
		commands.add(new ListResources(node, context));
		commands.add(new ListAgents(node, context));
		commands.add(new AddResource(node, context));
		commands.add(new AddConsumer(node, context));
		commands.add(new AddMarket(node, context));
		commands.add(new AddProducer(node, context));
		commands.add(new Remove(node, context));
		commands.add(new Elapsed(node, context));
		commands.add(new Remaining(node, context));
		commands.add(new Start(node, context));
		commands.add(new StartWait(node, context));
		commands.add(new Stop(node, context));
	}
	
	@SuppressWarnings("unchecked")
	protected void loadResources(){
		Map<String, Resource> resources = (Map<String, Resource>) context.get(SimulationCommand.RESOURCES);
		resources.put("Gold", new Resource("Mineral", "Gold"));
		resources.put("Copper", new Resource("Mineral", "Copper"));
		resources.put("Steel", new Resource("Alloy", "Steel"));
	}

}
