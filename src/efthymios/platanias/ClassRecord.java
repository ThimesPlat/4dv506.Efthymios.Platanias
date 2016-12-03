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

	@Override
	public String toString(){
		String result= "Class "+ this.name +"\nVariables: \n";
		for(VarRecord v: variables) result+=v.toString()+ "\n" ;
		result+="\nMethods:\n\t";
		for(MethodRecord m: methods)result+=m.toString()+"\n\t";
		return result;
	}
}
