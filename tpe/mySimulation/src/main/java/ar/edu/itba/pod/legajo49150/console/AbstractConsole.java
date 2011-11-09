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

import ar.edu.itba.node.Node;
import ar.edu.itba.pod.legajo49150.console.cmd.LoadScript;

public abstract class AbstractConsole<T extends Node> implements Runnable{

	private BufferedReader input;
	private AtomicBoolean finish = new AtomicBoolean(false);
	private List<Command<?>> commands = new ArrayList<Command<?>>();
	protected Map<String, Object> context = new HashMap<String, Object>();
	
	public AbstractConsole(InputStream input, T object){
		this.input = new BufferedReader(new InputStreamReader(input));
		loadContext(context);
		commands.add(new LoadScript<T>(null));
		loadCommands(commands, object, context);
	}

	protected abstract void loadContext(Map<String, Object> context);
	protected abstract void loadCommands(List<Command<?>> commands, T object, Map<String, Object> context);
	
	@Override
	public void run() {
		String line;
		List<String> args;
		try {
			while(!finish.get()){
				System.out.print(">> ");
				line = input.readLine();
				args = Arrays.asList(line.split(" "));
				commands.get(0).doChain(args, commands.subList(1, commands.size()));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
