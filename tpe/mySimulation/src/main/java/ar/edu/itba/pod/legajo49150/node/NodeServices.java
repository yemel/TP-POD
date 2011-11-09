package ar.edu.itba.pod.legajo49150.node;

import ar.edu.itba.balance.api.AgentsBalancer;
import ar.edu.itba.balance.api.AgentsTransfer;
import ar.edu.itba.event.RemoteEventDispatcher;
import ar.edu.itba.node.api.ClusterAdministration;
import ar.edu.itba.node.api.StatisticReports;

public class NodeServices {

	private ClusterAdministration cluster;
	private RemoteEventDispatcher dispatcher;
	private AgentsBalancer balancer;
	private AgentsTransfer transfer;
	private StatisticReports reports;
	
	public synchronized ClusterAdministration getAdmin() {
		return cluster;
	}
	public synchronized void setAdmin(ClusterAdministration cluster) {
		this.cluster = cluster;
	}
	public synchronized RemoteEventDispatcher getDispatcher() {
		return dispatcher;
	}
	public synchronized void setDispatcher(RemoteEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	public synchronized AgentsBalancer getBalancer() {
		return balancer;
	}
	public synchronized void setBalancer(AgentsBalancer balancer) {
		this.balancer = balancer;
	}
	public synchronized AgentsTransfer getTransfer() {
		return transfer;
	}
	public synchronized void setTransfer(AgentsTransfer transfer) {
		this.transfer = transfer;
	}
	public synchronized StatisticReports getReports() {
		return reports;
	}
	public synchronized void setReports(StatisticReports reports) {
		this.reports = reports;
	}
	
}
