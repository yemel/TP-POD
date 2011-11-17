package ar.edu.itba.pod.legajo49150.simulation.balance;

import ar.edu.itba.node.NodeInformation;

public class Election {

	public enum MsgType {CORD, ELECT}; 
	
	private final NodeInformation node;
	private final long timestamp;
	private final MsgType type;

	public Election(NodeInformation node, long timestamp, MsgType type) {
		this.node = node;
		this.timestamp = timestamp;
		this.type = type;
	}

	public NodeInformation getNode() {
		return node;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public boolean isElection(){
		return type.equals(MsgType.ELECT);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Election other = (Election) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
