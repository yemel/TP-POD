package ar.edu.itba.pod.legajo49150;

import org.joda.time.Duration;

import ar.edu.itba.pod.legajo49150.console.SimulationConsole;
import ar.edu.itba.pod.legajo49150.node.SimulationNode;
import ar.edu.itba.pod.time.TimeMapper;
import ar.edu.itba.pod.time.TimeMappers;

public class SimulationApp {

	public static void main(String[] args) throws Exception {
		if(args.length != 3){
			System.out.println("Invalid arguments");
			return;
		}
		
		if(System.getSecurityManager() == null){
			System.setSecurityManager(new SecurityManager());
		}
		
		TimeMapper timeMapper = TimeMappers.oneSecondEach(Duration.standardHours(6));
		SimulationNode node = SimulationNode.newNode(args[0], args[1], Integer.valueOf(args[2]),timeMapper);
		new SimulationConsole<SimulationNode>(node).run();
	}
	
}
