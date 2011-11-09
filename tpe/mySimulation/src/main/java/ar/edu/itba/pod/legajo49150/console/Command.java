package ar.edu.itba.pod.legajo49150.console;

import java.util.List;

import ar.edu.itba.node.Node;

public abstract class Command<T extends Node> {
	
	protected T node;
	
	protected Command(T node2){
		this.node = node2;
	}
	
	public void doChain(List<String> call, List<Command<?>> chain){
		if(call.get(0).equals(getName())){
			execute(call.subList(1, call.size()));
		}
		
		if(call.get(0).equals("commands")){
			System.out.println("-) " + getName() + " - " + getDescription() );
		}
		
		if(chain.size() > 0)
			chain.get(0).doChain(call, chain.subList(1, chain.size()));
	}
	
	protected abstract String getName();
	
	protected abstract String getDescription();
	
	protected abstract void execute(List<String> args);
	
}
