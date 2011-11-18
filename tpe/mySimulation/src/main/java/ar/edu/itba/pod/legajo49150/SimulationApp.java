package ar.edu.itba.pod.legajo49150;

import org.joda.time.Duration;

import ar.edu.itba.node.NodeInformation;
import ar.edu.itba.pod.legajo49150.console.SimulationConsole;
import ar.edu.itba.pod.legajo49150.node.NodeService;
import ar.edu.itba.pod.time.TimeMapper;
import ar.edu.itba.pod.time.TimeMappers;

public class SimulationApp {

	public static void main(String[] args) throws Exception {
		if(args.length != 3){
			System.out.println("Invalid arguments: <Name> <Host> <Port>");
			return;
		}
		
		securityInit();
		setRmiServerHostname(args[1]);
		
		TimeMapper timeMapper = TimeMappers.oneSecondEach(Duration.standardHours(6));
		NodeInformation nodeInfo= new NodeInformation(args[1], Integer.valueOf(args[2]), args[0]);
		
		NodeService node = new NodeService();
		node.loadServices(nodeInfo, timeMapper);
		
//		new SimulationConsole<SimulationNode>(node).run();
	}
	
	// TODO: Poner esto donde corresponda
	private static void securityInit() {
		if ( System.getProperty("java.security.policy") == null ) {
			System.setProperty("java.security.policy", "file.policy");
		}
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
	}
	
	private static void setRmiServerHostname(String host) {
		if ( System.getProperty("java.rmi.server.hostname") == null ) {
			System.setProperty("java.rmi.server.hostname", host);
		}
	}
	
}
