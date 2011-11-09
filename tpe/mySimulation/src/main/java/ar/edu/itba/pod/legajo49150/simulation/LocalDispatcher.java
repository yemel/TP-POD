package ar.edu.itba.pod.legajo49150.simulation;

import java.io.Serializable;

import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.legajo49150.node.SimulationNode;
import ar.edu.itba.pod.multithread.MultiThreadEventDispatcher;

public class LocalDispatcher extends MultiThreadEventDispatcher {

	private SimulationNode node;

	public LocalDispatcher(SimulationNode dispatcher) {
		this.node = dispatcher;
	}

	@Override
	public void publish(Agent source, Serializable event)
			throws InterruptedException {
		node.getDispatcher().broadcast(source, event);
		super.publish(source, event);
	}
}
