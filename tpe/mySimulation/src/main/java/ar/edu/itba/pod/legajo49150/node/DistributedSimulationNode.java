package ar.edu.itba.pod.legajo49150.node;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.pod.time.TimeMapper;

// TODO: Si no son muchos cambios podemos moverlo a SimulationNode
public class DistributedSimulationNode extends SimulationNode {

	// TODO: Abstraer
	public static DistributedSimulationNode newNode(String id, String host, int port, TimeMapper mapper) throws RemoteException, AlreadyBoundException {
		DistributedSimulationNode node = new DistributedSimulationNode(new NodeInformation(host, port, id), mapper);
		directory.publishAdmin(node, host, port);
		return node;
	}
	
	public DistributedSimulationNode(NodeInformation nodeInfo, TimeMapper maper) throws RemoteException {
		super(nodeInfo, maper);
		sim = new DistributedSimulationNode(nodeInfo, maper);
	}

	
	
}
