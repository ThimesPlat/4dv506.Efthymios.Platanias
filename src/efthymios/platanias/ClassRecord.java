package efthymios.platanias;

import java.util.HashSet;

public class ClassRecord extends Record {
	
	public HashSet<VarRecord> variables;
	public HashSet<MethodRecord> methods;	
	
	
	public ClassRecord(String n) {		
		super(n);		
		variables= new HashSet<VarRecord>();
		methods= new HashSet<MethodRecord>(); 
	}

}
