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

public class Poller implements Runnable {
	private Logger LOGGER = Logger.getLogger(Poller.class);
	
	private static Duration POLLING_TIME = Duration.millis(600);
	private static double POLLING_PROBABILITY = 0.1;

	private ClusterNode clusterNode;
	private RemoteEventDispatcher remoteDispatcher;
	
	public Poller(ClusterNode clusterNode, RemoteEventDispatcher remoteDispatcher){
		this.clusterNode = clusterNode;
		this.remoteDispatcher = remoteDispatcher;
	}
	
	@Override
	public void run() {
		while(true){
			try {
			Thread.sleep(POLLING_TIME.getMillis());
			Iterable<NodeInformation> nodes = Iterables.filter(clusterNode.connectedNodes(), randomPublish(POLLING_PROBABILITY, clusterNode.getNodeInfo()));
			for(NodeInformation node: nodes){
				RemoteEventDispatcher dispatcher = clusterNode.getDirectory().getDispatcher(node);
				Set<EventInformation> events = dispatcher.newEventsFor(clusterNode.getNodeInfo());
				for(EventInformation event: events){
//					LOGGER.info("Polled event from " + node.id());
					remoteDispatcher.publish(event);
				}
			}
			} catch (InterruptedException e) {
				LOGGER.info("Poller SHUTDOWN");
				return;
			} catch (Exception e) {
				LOGGER.error("Poller can't reach a node: " + e.getMessage());
			}
		}
	}
	
	// TODO: Este método está replicado en ClusterNode. Crear una libreria Utils
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
