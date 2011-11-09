package ar.edu.itba.pod.legajo49150.node;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import ar.edu.itba.node.Node;
import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.node.api.ClusterAdministration;
import ar.edu.itba.pod.doc.ThreadSafe;

import com.google.common.base.Preconditions;

@ThreadSafe
public class ClusterNode implements ClusterAdministration, Node {
	private final Logger LOGGER = Logger.getLogger(ClusterNode.class);

	private final NodeInformation nodeInfo;
	private final Set<NodeInformation> nodes = Collections.synchronizedSet(new HashSet<NodeInformation>());
	protected static NameResolver directory = new NameResolver();

	private String groupID;
	
	// TODO: Colección sincronizada (?)
	
	public static ClusterNode newNode(String id, String host, int port) throws RemoteException, AlreadyBoundException {
		ClusterNode node = new ClusterNode(new NodeInformation(host, port, id));
		directory.publishAdmin(node, host, port);
		return node;
	}
	
	public ClusterNode(NodeInformation nodeInfo) throws RemoteException {
		UnicastRemoteObject.exportObject(this, 0); // TODO: Esto es safe-publishing?
		this.nodeInfo = nodeInfo;
		nodes.add(nodeInfo);
	}
	
	@Override
	public synchronized void createGroup() throws RemoteException {
		Preconditions.checkState(getGroupId() == null, "The node is already connected to a group");
		Random rnd = new Random();
		String groupName = "group" + rnd.nextInt(1000);
		setGroupId(groupName);
		LOGGER.info("Grupo creado");
	}

	@Override
	public String getGroupId() throws RemoteException {
		return getGroupID();
	}

	@Override
	public boolean isConnectedToGroup() throws RemoteException {
		return (getGroupID() == null);
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
		if(this.nodes.contains(node)){ // TODO: Preguntar si esto tendría que ser fire-and-forget
			removeNode(node);
			for(NodeInformation each: this.nodes){
				ClusterAdministration other = directory.getAdmin(each);
				other.disconnectFromGroup(node);
			}
		}
		
		// TODO: Preguntar si alguien me puede desconectar a mi
		if(node.equals(this.nodeInfo)){
			disconnect();
		}
	}

	@Override
	public Set<NodeInformation> addNewNode(NodeInformation nodeInfo)
			throws RemoteException, NotBoundException {
		if(!this.nodes.contains(nodeInfo)){
			addNode(nodeInfo);
			for(NodeInformation each: this.nodes){
				ClusterAdministration node = directory.getAdmin(each);
				node.addNewNode(nodeInfo);
			}
		}
		return nodes;
	}

	@Override
	public Set<NodeInformation> connectedNodes() throws RemoteException {
		if(getGroupId() == null){
			return new HashSet<NodeInformation>();
		}
		
		return new HashSet<NodeInformation>(nodes);
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
