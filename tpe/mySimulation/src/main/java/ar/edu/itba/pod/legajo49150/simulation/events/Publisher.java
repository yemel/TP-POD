package ar.edu.itba.pod.legajo49150.simulation.events;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import ar.edu.itba.event.EventInformation;
import ar.edu.itba.event.RemoteEventDispatcher;
import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.pod.legajo49150.node.ClusterNode;
import ar.edu.itba.pod.legajo49150.node.Directory;
import ar.edu.itba.pod.legajo49150.node.NodeService;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class Publisher implements Runnable {
	private Logger LOGGER = Logger.getLogger(Publisher.class);

	private static Duration PUBLISHING_TIME = Duration.millis(200);
	private static double PUBLISH_PROBABILITY = 0.2;

	private ClusterNode clusterNode;
	private Directory directory;
	private RemoteEventDispatcher remoteDispatcher;

	public Publisher(NodeService services, RemoteEventDispatcher remoteDispatcher){
		this.clusterNode = services.getAdministrator();
		this.directory = services.getDirectory();
		this.remoteDispatcher = remoteDispatcher;
	}

	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(PUBLISHING_TIME.getMillis());
				Iterable<NodeInformation> nodes = Iterables.filter(clusterNode.connectedNodes(), randomPublish(PUBLISH_PROBABILITY, clusterNode.getNodeInfo()));
				for(NodeInformation node: nodes){
					try {
						pushEventsFor(node);
					} catch (Exception e) {
						clusterNode.disconnectNode(node);
					}
				}
			} catch (Exception e) {
				LOGGER.info("Publisher SHUTDOWN");
				return;
			}
		}
	}

	public void pushEventsFor(NodeInformation node) throws RemoteException, InterruptedException, NotBoundException{
		Set<EventInformation> events = remoteDispatcher.newEventsFor(node);
		RemoteEventDispatcher dispatcher = directory.getDispatcher(node);
		for(EventInformation event: events){
			dispatcher.publish(event);
		}
	}
	
	private static Predicate<NodeInformation> randomPublish(final double probability, final NodeInformation self){
		return new Predicate<NodeInformation>() {
			private Random rnd = new Random();
			@Override
			public boolean apply(NodeInformation arg0) {
				return !arg0.equals(self) && rnd.nextDouble() < probability;
			}
		};
	}
}
