package ar.edu.itba.pod.legajo49150.console;

import java.util.List;

public abstract class Command<T> {
	
	protected T nodeService;
	
	protected Command(T nodeService){
		this.nodeService = nodeService;
	}
	
	public boolean doChain(List<String> call, List<Command<T>> chain){
		boolean valid = false;
		if(call.get(0).equals(getName())){
			execute(call.subList(1, call.size()));
			valid = true;
		}
		
		if(call.get(0).equals("commands")){
			System.out.println("-) " + getName() + " - " + getDescription() );
		}
		
		if(chain.size() > 0)
			valid |= chain.get(0).doChain(call, chain.subList(1, chain.size()));
		return valid;
	}
	
	protected abstract String getName();
	
	protected abstract String getDescription();
	
	protected abstract void execute(List<String> args);
	
}
