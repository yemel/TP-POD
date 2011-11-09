package ar.edu.itba.pod.legajo49150.console;

import java.util.Map;

import ar.edu.itba.node.Node;
import ar.edu.itba.pod.agent.market.Resource;
import ar.edu.itba.pod.agent.runner.Agent;

public abstract class SimulationCommand<T extends Node> extends Command<T> {

	protected Map<String, Object> context;
	
	public static String RESOURCES = "resources";
	public static String AGENTS = "agents";
	
	protected SimulationCommand(T node, Map<String, Object> context) {
		super(node);
		this.context = context;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Resource> resources(){
		return (Map<String, Resource>) context.get(SimulationCommand.RESOURCES);
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String,Agent> agents(){
		return (Map<String, Agent>) context.get(SimulationCommand.AGENTS);
	}
}
