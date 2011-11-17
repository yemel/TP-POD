package ar.edu.itba.pod.legajo49150.simulation;

import org.joda.time.DateTime;

import ar.edu.itba.event.EventInformation;

public class TimedEventInformation {

	private DateTime time;
	private EventInformation eventInfo;
	
	public TimedEventInformation(EventInformation eventInfo, DateTime time) {
		this.time = time;
		this.eventInfo = eventInfo;
	}

	public EventInformation getEventInformation(){
		return eventInfo;
	}
	
	public DateTime getRecivedTime(){
		return time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((eventInfo == null) ? 0 : eventInfo.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return eventInfo.toString(); // TODO: Sacar este método, el toString está cableado
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimedEventInformation other = (TimedEventInformation) obj;
		if (eventInfo == null) {
			if (other.eventInfo != null)
				return false;
		} else if (!eventInfo.equals(other.eventInfo))
			return false;
		return true;
	}
	
	
}
