package ar.edu.itba.pod.legajo49150.simulation;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import ar.edu.itba.event.EventInformation;
import ar.edu.itba.event.RemoteEventDispatcher;
import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.pod.legajo49150.node.SimulationNode;

public class Publisher implements Runnable {

	private SimulationNode node;

	public Publisher(SimulationNode node){
		this.node = node;
	}

	@Override
	public void run() {
		try {
			while(true){
				Set<NodeInformation> nodes = node.getDispatcher().queues().keySet();
				for (NodeInformation nodeInfo : nodes) {
					RemoteEventDispatcher remote = node.getNameResolver().getDispatcher(nodeInfo);
					BlockingQueue<EventInformation> queue = node.getDispatcher().queues().get(nodeInfo);
					while(!queue.isEmpty()){
						remote.publish(queue.remove());
					}
					Thread.sleep(500); // TODO: Usar semaforos!
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
