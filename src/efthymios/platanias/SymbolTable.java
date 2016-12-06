package efthymios.platanias;


public class SymbolTable {
	private Scope root;      //Root of scope tree
	private Scope current;   //Current scope

	public SymbolTable(){
		root = new Scope(null); 
		current = root; 
	}
	
	public void enterScope() { 
		current = current.nextChild();
	}
	public void exitScope() { 
		current = current.getParent();
	}
	public void put(String key, Record item) { 
		current.put(key,item);
	}
	public Record lookup(String key) {
		return current.lookup(key); 
	}
	public void printTable() { 
		root.printScope(); 
	}
	public void resetTable(){ 
		root.resetScope(); 
	} 
	
	public Scope getCurrentScope(){
		return current;
	}

	
}
