package ar.edu.itba.pod.legajo49150.console;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import ar.edu.itba.pod.legajo49150.console.cmd.LoadScript;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public abstract class AbstractConsole implements Runnable{

	private final BufferedReader input;
	private final AtomicBoolean finish = new AtomicBoolean(false);
	private final List<Command<NodeService>> commands = new ArrayList<Command<NodeService>>();
	protected final Map<String, Object> context = new HashMap<String, Object>();
	protected final NodeService nodeService;
	
	public AbstractConsole(InputStream input, NodeService nodeService){
		this.input = new BufferedReader(new InputStreamReader(input));
		this.nodeService = nodeService;
		loadContext(context);
		commands.add(new LoadScript(nodeService));
		loadCommands(commands, nodeService, context);
	}

	protected abstract void loadContext(Map<String, Object> context);
	protected abstract void loadCommands(List<Command<NodeService>> commands, NodeService nodeService, Map<String, Object> context);
	
	@Override
	public void run() {
		boolean valid;
		String line;
		List<String> args;
		try {
			while(!finish.get()){
				System.out.print(">> ");
				line = input.readLine();
				args = Arrays.asList(line.split(" "));
				valid = commands.get(0).doChain(args, commands.subList(1, commands.size()));
				if(!valid){
					System.out.println("Invalid command");
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
