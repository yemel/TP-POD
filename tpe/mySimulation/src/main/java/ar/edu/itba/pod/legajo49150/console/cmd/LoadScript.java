package ar.edu.itba.pod.legajo49150.console.cmd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import ar.edu.itba.pod.legajo49150.console.Command;
import ar.edu.itba.pod.legajo49150.node.NodeService;

public class LoadScript extends Command<NodeService> {

	public LoadScript(NodeService node2) {
		super(node2);
	}

	@Override
	protected void execute(List<String> args) {}

	@Override
	public boolean doChain(List<String> call, List<Command<NodeService>> chain) {
		boolean valid = false;
		if(call.get(0).equals(getName())){
			try {
			List<String> args = call.subList(1, call.size());
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(args.get(0))));
			while(input.ready()){
				String line = input.readLine();
				System.out.println(">> " + line);
				args = Arrays.asList(line.split(" "));
				super.doChain(args, chain);
			}
			valid = true;
			} catch (IOException e){
				System.out.println("Problems reading de file");
			}
		}
		
		if(call.get(0).equals("commands")){
			System.out.println("-) " + getName() + " - " + getDescription() );
		}
		
		if(chain.size() > 0)
			valid |= chain.get(0).doChain(call, chain.subList(1, chain.size()));
		return valid;
		
	}

	@Override
	protected String getDescription() {
		return "Load a command script";
	}

	@Override
	protected String getName() {
		return "load";
	}

}
