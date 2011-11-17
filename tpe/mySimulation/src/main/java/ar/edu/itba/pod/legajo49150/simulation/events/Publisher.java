package ar.edu.itba.pod.legajo49150.simulation.events;

import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Duration;

import ar.edu.itba.event.EventInformation;
import ar.edu.itba.event.RemoteEventDispatcher;
import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.pod.legajo49150.node.ClusterNode;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class Publisher implements Runnable {
	private Logger LOGGER = Logger.getLogger(Publisher.class);

	private static Duration PUBLISHING_TIME = Duration.millis(200);
	private static double PUBLISH_PROBABILITY = 0.2;

	private ClusterNode clusterNode;
	private RemoteEventDispatcher remoteDispatcher;

	public Publisher(ClusterNode clusterNode, RemoteEventDispatcher remoteDispatcher){
		this.clusterNode = clusterNode;
		this.remoteDispatcher = remoteDispatcher;
	}

	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(PUBLISHING_TIME.getMillis());
				Iterable<NodeInformation> nodes = Iterables.filter(clusterNode.connectedNodes(), randomPublish(PUBLISH_PROBABILITY, clusterNode.getNodeInfo()));
				for(NodeInformation node: nodes){
					Set<EventInformation> events = remoteDispatcher.newEventsFor(node);
					RemoteEventDispatcher dispatcher = clusterNode.getDirectory().getDispatcher(node);
					for(EventInformation event: events){
						dispatcher.publish(event);
					}
				}
			} catch (InterruptedException e) {
				LOGGER.info("Publisher SHUTDOWN");
				return;
			} catch (Exception e) {
				LOGGER.error("Publisher can't reach a node: " + e.getMessage());
			}
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
