package ar.edu.itba.pod.legajo49150.node;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ar.edu.itba.balance.api.AgentsBalancer;
import ar.edu.itba.balance.api.AgentsTransfer;
import ar.edu.itba.event.RemoteEventDispatcher;
import ar.edu.itba.node.Node;
import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.node.api.ClusterAdministration;
import ar.edu.itba.node.api.StatisticReports;

public class Directory {

	private Registry registry;
	private ConcurrentMap<NodeInformation, NodeServices> cache =
			new ConcurrentHashMap<NodeInformation, NodeServices>();

	public void publishAdmin(ClusterNode node, String host, int port) throws RemoteException, AlreadyBoundException{
		Registry registry = getRegistry(host, port);
		registry.bind(Node.CLUSTER_COMUNICATION, node);
	}

	public void publishDispatcher(RemoteEventDispatcher dispatcher, String host, int port) throws RemoteException, AlreadyBoundException{
		Registry registry = getRegistry(host, port);
		registry.bind(Node.DISTRIBUTED_EVENT_DISPATCHER, dispatcher);
	}
	
	public void publishBalancer(AgentsBalancer balancer, String host, int port) throws RemoteException, AlreadyBoundException{
		Registry registry = getRegistry(host, port);
		registry.bind(Node.AGENTS_BALANCER, balancer);
	}
	
	public void publishTransfer(AgentsTransfer transfer, String host, int port) throws RemoteException, AlreadyBoundException{
		Registry registry = getRegistry(host, port);
		registry.bind(Node.AGENTS_TRANSFER, transfer);
	}
	
	public void publishStatistics(StatisticReports statistics, String host, int port) throws RemoteException, AlreadyBoundException{
		Registry registry = getRegistry(host, port);
		registry.bind(Node.STATISTIC_REPORTS, statistics);
	}

	//TODO: Abstraer esto
	public ClusterAdministration getAdmin(NodeInformation nodeInfo) throws RemoteException, NotBoundException{
		NodeServices services = getService(nodeInfo);
		ClusterAdministration admin = services.getAdmin();
		if(admin == null){
			final Registry reg = LocateRegistry.getRegistry(nodeInfo.host(), nodeInfo.port());
			admin = (ClusterAdministration) reg.lookup(Node.CLUSTER_COMUNICATION);
			services.setAdmin(admin);
		}
		return admin;
	}

	public RemoteEventDispatcher getDispatcher(NodeInformation nodeInfo) throws RemoteException, NotBoundException{
		NodeServices services = getService(nodeInfo);
		RemoteEventDispatcher dispatcher = services.getDispatcher();
		if(dispatcher == null){
			final Registry reg = LocateRegistry.getRegistry(nodeInfo.host(), nodeInfo.port());
			dispatcher = (RemoteEventDispatcher) reg.lookup(Node.DISTRIBUTED_EVENT_DISPATCHER);
			services.setDispatcher(dispatcher);
		}
		return dispatcher;
	}

	public AgentsBalancer getBalancer(NodeInformation nodeInfo) throws RemoteException {
		try {
			NodeServices services = getService(nodeInfo);
			AgentsBalancer balancer = services.getBalancer();
			if(balancer == null){
				final Registry reg = LocateRegistry.getRegistry(nodeInfo.host(), nodeInfo.port());
				balancer = (AgentsBalancer) reg.lookup(Node.AGENTS_BALANCER);
				services.setBalancer(balancer);
			}
			return balancer;
		} catch (NotBoundException e) {
			throw new RuntimeException("Not Bound???" + e);
		}
	}
	
	public AgentsTransfer getTransfer(NodeInformation nodeInfo) throws RemoteException {
		try {
			NodeServices services = getService(nodeInfo);
			AgentsTransfer transfer = services.getTransfer();
			if(transfer == null){
				final Registry reg = LocateRegistry.getRegistry(nodeInfo.host(), nodeInfo.port());
				transfer = (AgentsTransfer) reg.lookup(Node.AGENTS_TRANSFER);
				services.setTransfer(transfer);
			}
			return transfer;
		} catch (NotBoundException e) {
			throw new RuntimeException("Not Bound???" + e);
		}
	}

	private NodeServices getService(NodeInformation nodeInfo){
		NodeServices services = cache.get(nodeInfo);
		if(services == null){
			services = new NodeServices();
			cache.put(nodeInfo, services);
		}
		return services;
	}

	private Registry getRegistry(String host, int port) throws RemoteException {
		if(registry == null){
			registry = LocateRegistry.createRegistry(port);
		}
		return registry;
	}
}
