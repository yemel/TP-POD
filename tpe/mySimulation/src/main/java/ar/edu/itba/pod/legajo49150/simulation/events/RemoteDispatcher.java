package ar.edu.itba.pod.legajo49150.simulation.events;

import java.rmi.NotBoundException;
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
import ar.edu.itba.pod.doc.ThreadSafe;
import ar.edu.itba.pod.legajo49150.node.NodeService;
import ar.edu.itba.pod.legajo49150.simulation.LocalDispatcher;
import ar.edu.itba.pod.legajo49150.simulation.TimedEventInformation;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@ThreadSafe
public class RemoteDispatcher implements RemoteEventDispatcher {

	private final Logger LOGGER = Logger.getLogger(RemoteDispatcher.class);

	private final LocalDispatcher localDispatcher;

	private final Queue<TimedEventInformation> messages = new ConcurrentLinkedQueue<TimedEventInformation>(); 
	private final Map<NodeInformation, DateTime> calendar = new ConcurrentHashMap<NodeInformation, DateTime>();
	private final Thread publisherT, pollerT;
	private final Publisher publisher;
	private final Poller poller;

	public RemoteDispatcher(LocalDispatcher dispatcher, NodeService services) throws RemoteException{
		this.localDispatcher = dispatcher;
		this.publisher = new Publisher(services, this);
		this.publisherT = new Thread(this.publisher);
		this.poller = new Poller(services, this);
		this.pollerT = new Thread(this.poller);
		// TODO: Implementar el Cleaner que limpie los mensajes!
		UnicastRemoteObject.exportObject(this, 0);
	}

	@Override
	public BlockingQueue<Object> moveQueueFor(Agent agent)
			throws RemoteException {
		BlockingQueue<Object> queue = localDispatcher.deregister(agent);
		LOGGER.info("Retorno la cola de eventos para el agente " + agent.name() + " (" + queue.size() + " eventos)");
		return queue;
	}

	public void setQueueFor(Agent agent, BlockingQueue<Object> queue){
		LOGGER.info("Seteo la cola de eventos para el agente " + agent.name() + " (" + queue.size() + " eventos)");
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
	public synchronized boolean publish(EventInformation event) throws RemoteException,
	InterruptedException {
		TimedEventInformation ev = new TimedEventInformation(event, new DateTime());
		if(!messages.contains(ev)){
			messages.add(ev);
			LOGGER.debug("Publico nuevo evento a la simulación local: " + event);
			localDispatcher.localPublish(event.source(), event.event());
			return false;
		}
		return true;
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

	public synchronized void start(){
		publisherT.start();
		pollerT.start();
	}

	public synchronized void stop(){
		try {
			publisherT.interrupt();
			pollerT.interrupt();
			publisherT.join();
			pollerT.join();
		} catch (InterruptedException e) {
			LOGGER.error("Can't join with a worker thread: " + e.getMessage());
		}
	}

	public synchronized void synchronizeWith(NodeInformation node){
		try {
			this.poller.pollEventsFor(node);
			this.publisher.pushEventsFor(node);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
