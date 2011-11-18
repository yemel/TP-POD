package ar.edu.itba.pod.legajo49150.simulation;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.joda.time.DateTime;

import ar.edu.itba.event.EventInformation;
import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.legajo49150.node.NodeService;
import ar.edu.itba.pod.multithread.MultiThreadEventDispatcher;

public class LocalDispatcher extends MultiThreadEventDispatcher {

	private NodeService services;

	public LocalDispatcher(NodeService services) {
		this.services = services;
	}

	@Override
	public void publish(Agent source, Serializable event)
			throws InterruptedException {
		EventInformation eventInfo = new EventInformation(event, services.getAdministrator().getNodeInfo().id(), source);
		eventInfo.setReceivedTime(new DateTime().getMillis());
		try {
			services.getDispatcher().publish(eventInfo);
		} catch (RemoteException e) {
			// Do nothing
		}
	}
	
	public void localPublish(Agent source, Serializable event) throws InterruptedException{
		super.publish(source, event);
	}
}
