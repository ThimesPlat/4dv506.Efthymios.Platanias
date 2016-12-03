package efthymios.platanias;

import java.util.ArrayList;
import java.util.HashMap;

public class Scope {

	private int next = 0;
	private Scope parent;
	private ArrayList<Scope> children = new ArrayList<Scope>();
	private HashMap<String, Record> records = new HashMap<String, Record>();

	public Scope() {
	}

	public Scope(Scope p) {
		parent=p;
	}

	public Scope nextChild() {
		// Creates new children on demand
		Scope nextC;
		if (next >= children.size()) { // Child does not exist
			nextC = new Scope(this); // ==> create new Scope
			children.add(nextC);
		} else
			nextC = children.get(next); // Child exists ==> visit child
		next++;
		return nextC;
	}

	public Record lookup(String key) {
		if (records.containsKey(key)) // Check if in current scope 
			return records.get(key);
		
		else { 							// Move to enclosing/parent scope
			if (parent == null)
				return null; 			// Identifier not in table
			else
				return parent.lookup(key); // Delegate request to enclosing
											// scope
		}
	}

	public void resetScope() {
		next = 0;
		for (int i = 0; i < children.size(); i++)
			(children.get(i)).resetScope();
	}

}
