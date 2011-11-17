package ar.edu.itba.pod.legajo49150.simulation.events;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import ar.edu.itba.event.EventInformation;
import ar.edu.itba.event.RemoteEventDispatcher;
import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.pod.agent.runner.Agent;
import ar.edu.itba.pod.legajo49150.node.NodeService;
import ar.edu.itba.pod.legajo49150.simulation.LocalDispatcher;
import ar.edu.itba.pod.legajo49150.simulation.TimedEventInformation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class RemoteDispatcher implements RemoteEventDispatcher {

	private Logger LOGGER = Logger.getLogger(RemoteDispatcher.class);

	private LocalDispatcher localDispatcher;

	private Queue<TimedEventInformation> messages = new ConcurrentLinkedQueue<TimedEventInformation>(); 
	private Map<NodeInformation, DateTime> calendar = new ConcurrentHashMap<NodeInformation, DateTime>();
	private Thread publisher, poller;

	public RemoteDispatcher(LocalDispatcher dispatcher, NodeService services) throws RemoteException{
		this.localDispatcher = dispatcher;
		this.publisher = new Thread(new Publisher(services.getAdministrator(), this));
		this.poller = new Thread(new Poller(services.getAdministrator(), this));
		// TODO: Implementar el Cleaner que limpie los mensajes!
		UnicastRemoteObject.exportObject(this, 0);
	}

	@Override
	public BlockingQueue<Object> moveQueueFor(Agent agent)
			throws RemoteException {
		// Sincronizar eventos entre ambos, pull, push.
		// Sacar la cola de eventos
		BlockingQueue<Object> queue = localDispatcher.deregister(agent);
		// Retornar la cola de eventos
		return queue;
	}

	public void setQueueFor(Agent agent, BlockingQueue<Object> queue){
		localDispatcher.setAgentQueue(agent, queue);
	}
	
	@Override
	public Set<EventInformation> newEventsFor(NodeInformation nodeInformation) throws RemoteException {
		DateTime lastTime = calendar.get(nodeInformation);
		Iterable<TimedEventInformation> events = Iterables.filter(messages, newEventsPredicate(lastTime, nodeInformation));
		calendar.put(nodeInformation, new DateTime());
		return Sets.newHashSet(Iterables.transform(events, toEventInformation));
	}

	@Override
	public void publish(EventInformation event) throws RemoteException,
	InterruptedException {
		TimedEventInformation ev = new TimedEventInformation(event, new DateTime());
		if(!messages.contains(ev)){
			messages.add(ev);
//			LOGGER.info("Reciving event " + event);
			localDispatcher.localPublish(event.source(), event.event());
		}
		
		// TODO: El método tendría que devolver true o false! Bajar de IOL
	}

	private static Predicate<TimedEventInformation> newEventsPredicate(final DateTime lastTime, final NodeInformation node){
		return new Predicate<TimedEventInformation>() {
			@Override
			public boolean apply(TimedEventInformation arg0) {
				if(!node.id().equals(arg0.getEventInformation().nodeId()))
					return lastTime == null || arg0.getRecivedTime().isAfter(lastTime);
				return false;
			}
		};
	}

	private static Function<TimedEventInformation, EventInformation> toEventInformation =
			new Function<TimedEventInformation, EventInformation>() {
		@Override
		public EventInformation apply(TimedEventInformation from) {
			return from.getEventInformation();
		}
	};

	public void start(){
		publisher.start();
		poller.start();
	}

	public void stop(){
		try {
			publisher.interrupt();
			poller.interrupt();
			publisher.join();
			poller.join();
		} catch (InterruptedException e) {
			LOGGER.error("Can't join with a worker thread: " + e.getMessage());
		}
	}

}
