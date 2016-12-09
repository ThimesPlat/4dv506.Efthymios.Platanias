package efthymios.platanias;

import java.util.HashMap;
import java.util.Map;

public class ClassRecord extends Record{
	
	//Scope scope;
	Map<String,VarRecord> variables = new HashMap<String,VarRecord>();
	Map<String,MethodRecord> methods = new HashMap<String,MethodRecord>();
	
	public ClassRecord(String name, String returnType){
		super(name, name);
	}

	public MethodRecord getMethod(String name) {
		return methods.get(name);
	}

	public void setMethods(Map<String,MethodRecord> methods) {
		this.methods = methods ; 
	}

	public VarRecord getVariable(String Varname) {
		return variables.get(Varname);
	}

	public void setVariables(Map<String, VarRecord> variables) {
		this.variables = variables;
	}
	
	@Override
	public String toString(){
		String result ="\n~~~~~~~~~~~~~~~~~~ CLASS RECORD ~~~~~~~~~~~~~~~~~~\n\n";
		result += "Class "+ this.name +"\nVariables:\n";
		for(Map.Entry<String, VarRecord> v: variables.entrySet())
			result+=v.getValue().toString()+ "\n" ;
		result+="\nMethods:\n";
		for(Map.Entry<String,MethodRecord> m: methods.entrySet())
			result+=m.toString()+"\n";
		result+="\n~~~~~~~~~~~~~~~~~~ END CLASS RECORD ~~~~~~~~~~~~~~~~~~\n\n";
		return result;
	}
}