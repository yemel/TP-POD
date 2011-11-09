package ar.edu.itba.pod.legajo49150.node;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ar.edu.itba.event.RemoteEventDispatcher;
import ar.edu.itba.node.Node;
import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.node.api.ClusterAdministration;

public class NameResolver {

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
