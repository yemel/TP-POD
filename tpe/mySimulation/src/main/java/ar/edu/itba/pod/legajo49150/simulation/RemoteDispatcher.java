package ar.edu.itba.pod.legajo49150.simulation;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import ar.edu.itba.event.EventInformation;
import ar.edu.itba.event.RemoteEventDispatcher;
import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.legajo49150.node.ClusterNode;
import ar.edu.itba.pod.legajo49150.node.NameResolver;
import ar.edu.itba.pod.multithread.EventDispatcher;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

public class RemoteDispatcher implements RemoteEventDispatcher {
	protected static final int QUEUE_SIZE = 256;

	private Logger LOGGER = Logger.getLogger(RemoteDispatcher.class);
	
	private EventDispatcher dispatcher;
	private ClusterNode node;
	
	private Deque<Serializable> history = new LinkedBlockingDeque<Serializable>();
	private Map<NodeInformation, BlockingQueue<EventInformation>> queues = new MapMaker()
	.makeComputingMap(new Function<NodeInformation, BlockingQueue<EventInformation>>() { 
		@Override
		public BlockingQueue<EventInformation> apply(NodeInformation input) {
			return new LinkedBlockingQueue<EventInformation>(QUEUE_SIZE);
		}
	});

	public RemoteDispatcher(EventDispatcher dispatcher, ClusterNode node, NameResolver directory) throws RemoteException{
		this.dispatcher = dispatcher;
		this.node = node;
		UnicastRemoteObject.exportObject(this, 0);
	}

	@Override
	public BlockingQueue<Object> moveQueueFor(Agent agent)
	throws RemoteException {
		return null;
	}

	@Override
	public Set<EventInformation> newEventsFor(NodeInformation nodeInformation)
	throws RemoteException {
		return null;
	}

	@Override
	public void publish(EventInformation event) throws RemoteException,
	InterruptedException {
		// Seteo el tiempo de recepción
		if(!event.isOld(0)){
			event.setReceivedTime(System.currentTimeMillis());
		}
		
		if(!event.isOld(System.currentTimeMillis())){
			dispatcher.publish(event.source(), event.event());
			LOGGER.info("Llegó el evento: [" + event.nodeId() + ", " + event.event() + "]");
		}
	}

	public void broadcast(Agent source, Serializable event) throws InterruptedException{
		EventInformation evInfo = new EventInformation(event, node.getNodeInfo().id(), source);

		Set<NodeInformation> nodes = queues.keySet();
		for (NodeInformation node : nodes) {
			BlockingQueue<EventInformation> queue = queues.get(node);
			queue.put(evInfo);
		}
	}

	public void registerNode(NodeInformation nodeInfo){
		if(!nodeInfo.equals(node.getNodeInfo())){
			queues.get(nodeInfo);
		}
	}

	public void unregisterNode(NodeInformation nodeInfo){
		queues.remove(nodeInfo);
	}

	public Map<NodeInformation, BlockingQueue<EventInformation>> queues(){
		return queues;
	}
	
}
