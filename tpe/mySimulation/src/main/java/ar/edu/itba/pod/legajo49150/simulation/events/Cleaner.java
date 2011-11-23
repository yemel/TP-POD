package ar.edu.itba.pod.legajo49150.simulation.events;

import java.util.Queue;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import ar.edu.itba.pod.legajo49150.simulation.TimedEventInformation;

public class Cleaner implements Runnable {

	private final Duration CLEANING_TIME = Duration.standardSeconds(10); 
	private final Queue<TimedEventInformation> queue;

	public Cleaner(Queue<TimedEventInformation> queue){
		this.queue = queue;
	}

	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(CLEANING_TIME.getMillis());
				DateTime limit = DateTime.now().minus(Duration.standardSeconds(10));
				for(TimedEventInformation event: queue){
					if(event.getRecivedTime().isBefore(limit)){
						queue.remove(event);
					}
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	}

}
