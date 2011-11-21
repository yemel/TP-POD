package ar.edu.itba.pod.legajo49150.console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.edu.itba.pod.agent.market.Resource;
import ar.edu.itba.pod.agent.market.ResourceAgent;
import ar.edu.itba.pod.legajo49150.console.cmd.cluster.Connect;
import ar.edu.itba.pod.legajo49150.console.cmd.cluster.ConnectedNodes;
import ar.edu.itba.pod.legajo49150.console.cmd.cluster.CreateGroup;
import ar.edu.itba.pod.legajo49150.console.cmd.cluster.Disconnect;
import ar.edu.itba.pod.legajo49150.console.cmd.other.Coordinator;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.AddConsumer;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.AddFactory;
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
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.Statistics;
import ar.edu.itba.pod.legajo49150.console.cmd.simulation.Stop;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class SimulationConsole extends AbstractConsole {

	public SimulationConsole(NodeService node) {
		super(System.in, node);
	}
	
	@Override
	protected void loadContext(Map<String, Object> context) {
		Map<String, Resource> resources = new HashMap<String, Resource>();
		Map<String, ResourceAgent> agents = new HashMap<String, ResourceAgent>();
		context.put(ContextCommand.RESOURCES, resources);
		context.put(ContextCommand.AGENTS, agents);
		loadResources();
	}

	@SuppressWarnings("unchecked")
	protected void loadResources(){
		Map<String, Resource> resources = (Map<String, Resource>) context.get(ContextCommand.RESOURCES);
		resources.put("Gold", new Resource("Mineral", "Gold"));
		resources.put("Copper", new Resource("Mineral", "Copper"));
		resources.put("Steel", new Resource("Alloy", "Steel"));
	}

	@Override
	protected void loadCommands(List<Command<NodeService>> commands,
			NodeService nodeService, Map<String, Object> context) {
		commands.add(new CreateGroup(nodeService));
		commands.add(new Connect(nodeService));
		commands.add(new Disconnect(nodeService));
		commands.add(new ConnectedNodes(nodeService));	
		commands.add(new Coordinator(nodeService, context));
		commands.add(new Statistics(nodeService, context));
		commands.add(new ListResources(nodeService, context));
		commands.add(new ListAgents(nodeService, context));
		commands.add(new AddResource(nodeService, context));
		commands.add(new AddConsumer(nodeService, context));
		commands.add(new AddMarket(nodeService, context));
		commands.add(new AddProducer(nodeService, context));
		commands.add(new AddFactory(nodeService, context));
		commands.add(new Remove(nodeService, context));
		commands.add(new Elapsed(nodeService, context));
		commands.add(new Remaining(nodeService, context));
		commands.add(new Start(nodeService, context));
		commands.add(new StartWait(nodeService, context));
		commands.add(new Stop(nodeService, context));
	}

}
