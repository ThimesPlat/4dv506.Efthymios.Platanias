package efthymios.platanias;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//import java.util.Map.Entry;

public class Scope {
	private String name;
	private int next = 0;                      //Next child to visit
	private Scope parent;                      //Parent scope
	private List<Scope> children = new ArrayList<Scope>();   //Children scopes
	private Map<String,Record> records = new HashMap<String,Record>();       //Symbol to Record map

	Scope (Scope temp){
		if(temp!=null){
			parent = temp.parent;
		}		
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	
	public String getName(){
		return name;
	}
	
	public Scope nextChild() {      //Creates new children on demand
		Scope nextC;
		if (next >= children.size()) {           //Child does not exist
			nextC = new Scope(this);               // ==> create new Scope
			children.add(nextC);
		}else                                     //Child exists
			nextC = (Scope) children.get(next);    // ==> visit child
		next++;
		return nextC;
	}

	public Record lookup(String name) {
		if (records.containsKey(name))        //Check if in current scope
			return (Record) records.get(name);
		else {                               //Move to enclosing/parent scope
			if (parent == null)
				return null;                 // Identifier not in table
			else
				return parent.lookup(name);   // Delegate request to enclosing scope
		}
	}
	
	public int getNext() {
		return next;
	}

	public void setNext(int next) {
		this.next = next;
	}

	public Scope getParent() {
		return parent;
	}

	public void setParent(Scope parent) {
		this.parent = parent;
	}

	public List<Scope> getChildren() {
		return children;
	}

	public void setChildren(List<Scope> children) {
		this.children = children;
	}
	
	public void put(String name, Record item){
		records.put(name, item);
	}
	
	public void resetScope() {  // Must be called after each traversal
		next = 0;
		for (int i=0;i<children.size();i++)
		((Scope)children.get(i)).resetScope();
	}
/*
	public void printScope() {
		Iterator<Entry<String, Record>> it = records.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Record> pair = (Map.Entry<String, Record>)it.next();
	        System.out.println(pair.getKey());
	    }
	}
*/
	
	public void printScope(){
        for (Map.Entry<String, Record> entry : records.entrySet()) {
            String key = entry.getKey();
            Record value = entry.getValue();
            //Record value = entry.getValue();
            System.out.print(key+" "+value.toString()+"\n\n");
        }
        Iterator<Scope> iterator = children.iterator(); 
        while (iterator.hasNext()) {
            iterator.next().printScope();
        }
    }
}