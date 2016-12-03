package efthymios.platanias;

import java.util.HashSet;

public class MethodRecord extends Record {
	
	private String type;
	public HashSet<VarRecord> variables;

	public MethodRecord(String n, String t) {
		super(n);
		type=t;
		variables= new HashSet<VarRecord>();
	}
	
	public String getType() {return type;}
	
	public String toString(){
		String result= "Method: "+name+" "+type+"\nVariables:";
		for(VarRecord v: variables) result+="\n\t"+v.toString();
		return result;
	}
	

}
