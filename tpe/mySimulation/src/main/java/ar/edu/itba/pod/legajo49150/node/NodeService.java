package ar.edu.itba.pod.legajo49150.node;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicReference;

import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.pod.doc.ThreadSafe;
import ar.edu.itba.pod.legajo49150.simulation.LocalDispatcher;
import ar.edu.itba.pod.legajo49150.simulation.balance.ClusterBalancer;
import ar.edu.itba.pod.legajo49150.simulation.events.RemoteDispatcher;
import ar.edu.itba.pod.legajo49150.simulation.statistics.ClusterStatistics;
import ar.edu.itba.pod.legajo49150.simulation.transfer.ClusterTransfer;
import ar.edu.itba.pod.time.TimeMapper;

@ThreadSafe
public class NodeService {

	private final AtomicReference<SimulationNode> simNode = new AtomicReference<SimulationNode>();
	private final AtomicReference<RemoteDispatcher> dispatcher = new AtomicReference<RemoteDispatcher>();
	private final AtomicReference<ClusterBalancer> balancer = new AtomicReference<ClusterBalancer>();
	private final AtomicReference<ClusterTransfer> transfer = new AtomicReference<ClusterTransfer>();
	private final AtomicReference<ClusterStatistics> statistics = new AtomicReference<ClusterStatistics>();
	private final AtomicReference<Directory> directory = new AtomicReference<Directory>();
	
	public NodeService(){
		this.directory.set(new Directory());
	}
	
	private void loadServices(NodeInformation nodeInfo, TimeMapper maper) throws RemoteException{
		this.simNode.set(new SimulationNode(nodeInfo, maper));
		directory.get().publishAdmin(simNode.get(), nodeInfo.host(), nodeInfo.port());
		
		LocalDispatcher localDispatcher = new LocalDispatcher(this);
		this.dispatcher.set(new RemoteDispatcher(localDispatcher, this));
		directory.publishDispatcher(dispatcher.get(), nodeInfo.host(), nodeInfo.port());
		
		this.balancer.set(new ClusterBalancer(this));
		this.balancer.get().start();
		directory.publishBalancer(balancer, nodeInfo.host(), nodeInfo.port());
		
		this.transfer.set(new ClusterTransfer(this));
		directory.publishTransfer(transfer, nodeInfo.host(), nodeInfo.port());
		
		this.statistics.set(new ClusterStatistics(this));
		directory.publishStatistics(statistics, nodeInfo.host(), nodeInfo.port());
	}

	public SimulationNode getAdministrator() {
		if(simNode == null)
			throw new IllegalStateException("The object is not ready yet");
		return simNode.get();
	}

	public RemoteDispatcher getDispatcher() {
		if(dispatcher == null)
			throw new IllegalStateException("The object is not ready yet");
		return dispatcher.get();
	}

	public ClusterBalancer getBalancer() {
		if(balancer == null)
			throw new IllegalStateException("The object is not ready yet");
		return balancer.get();
	}

	public ClusterTransfer getTransfer() {
		if(transfer == null)
			throw new IllegalStateException("The object is not ready yet");
		return transfer.get();
	}

	public ClusterStatistics getStatistics() {
		if(statistics == null)
			throw new IllegalStateException("The object is not ready yet");
		return statistics.get();
	}

	public Directory getDirectory() {
		if(directory == null)
			throw new IllegalStateException("The object is not ready yet");
		return directory.get();
	}
}
