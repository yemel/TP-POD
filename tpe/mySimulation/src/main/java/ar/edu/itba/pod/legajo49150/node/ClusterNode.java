package ar.edu.itba.pod.legajo49150.node;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.node.api.ClusterAdministration;
import ar.edu.itba.pod.doc.ThreadSafe;

import com.google.common.base.Preconditions;

@ThreadSafe
public class ClusterNode implements ClusterAdministration {
	private final Logger LOGGER = Logger.getLogger(ClusterNode.class);

	private final NodeInformation nodeInfo;
	private final Directory directory;
	private final NodeService service;
	private final Set<NodeInformation> nodes = Collections.newSetFromMap(new ConcurrentHashMap<NodeInformation, Boolean>());

	private String groupID;

	public ClusterNode(NodeInformation nodeInfo, NodeService service) throws RemoteException {
		this.nodeInfo = nodeInfo;
		this.service = service;
		this.directory = service.getDirectory();
		nodes.add(nodeInfo);
		UnicastRemoteObject.exportObject(this, 0);
	}

	@Override
	public synchronized void createGroup() throws RemoteException {
		Preconditions.checkState(getGroupId() == null, "The node is already connected to a group");
		Random rnd = new Random();
		String groupName = "group" + rnd.nextInt(1000);
		setGroupId(groupName);
	}

	@Override
	public String getGroupId() throws RemoteException {
		return getGroupID();
	}

	@Override
	public boolean isConnectedToGroup() throws RemoteException {
		return (getGroupID() != null);
	}

	@Override
	public void connectToGroup(String host, int port) throws RemoteException,
	NotBoundException {
		ClusterAdministration node = directory.getAdmin(new NodeInformation(host, port, "NOSE!")); // TODO!
		setGroupId(node.getGroupId());	// TODO: Preguntar si esto puede tirar IllegalState
		Set<NodeInformation> nodes = node.addNewNode(nodeInfo);
		for(NodeInformation each: nodes){
			addNode(each);
		}
	}

	@Override
	public void disconnectFromGroup(NodeInformation node)
			throws RemoteException, NotBoundException {
		if(this.nodes.contains(node)){
			removeNode(node);
			for(NodeInformation each: this.nodes){
				try{
					ClusterAdministration other = directory.getAdmin(each);
					other.disconnectFromGroup(node);
				} catch(Exception e) { disconnectNode(each); }
			}
		}

		// TODO: Preguntar si alguien me puede desconectar a mi
		if(node.equals(this.nodeInfo)){
			disconnect();
		}

		if(service.getBalancer().getCoordinator().equals(node)){
			service.getBalancer().clearCoordinator();
		}
	}

	@Override
	public Set<NodeInformation> addNewNode(NodeInformation nodeInfo)
			throws RemoteException, NotBoundException {
		if(!this.nodes.contains(nodeInfo)){
			addNode(nodeInfo);
			for(NodeInformation each: this.nodes){
				try {
					ClusterAdministration node = directory.getAdmin(each);
					node.addNewNode(nodeInfo);
				} catch(Exception e) { disconnectNode(each); }
			}
		}
		synchronized (service.getBalancer()) {} // syncronizo con el balancer
		return nodes;
	}

	@Override
	public Set<NodeInformation> connectedNodes() throws RemoteException {
		if(getGroupId() == null){
			return new HashSet<NodeInformation>();
		}

		return new HashSet<NodeInformation>(nodes);
	}

	public void disconnectNode(final NodeInformation nodeInfo){
		try {
			// Check if the node is alive!
			ClusterAdministration admin = service.getDirectory().getAdmin(nodeInfo);
			admin.getGroupId();
		} catch (Exception e) {
			LOGGER.error("The node " + nodeInfo + " is not responding! Lets disconnect it");
			new Thread(new Runnable(){
				@Override
				public void run() {try { disconnectFromGroup(nodeInfo); } catch (Exception e) {}}
			}).run();
		}
	}

	private void disconnect(){
		this.nodes.clear();
		this.nodes.add(nodeInfo);
		setGroupId(null);
	}

	private synchronized String getGroupID() {
		return groupID;
	}

	public synchronized void setGroupId(String groupID) {
		this.groupID = groupID;
	}

	public NodeInformation getNodeInfo() {
		return nodeInfo;
	}

	protected void addNode(NodeInformation nodeInfo){
		this.nodes.add(nodeInfo);
	}

	protected void removeNode(NodeInformation nodeInfo){
		this.nodes.remove(nodeInfo);
	}
}
