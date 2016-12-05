package efthymios.platanias;

import java.util.HashMap;
import java.util.Map;

public class ClassRecord extends Record{
	
	//Scope scope;
	Map<String,Record> variables = new HashMap<String,Record>();
	Map<String,MethodRecord> methods = new HashMap<String,MethodRecord>();
	
	public ClassRecord(String name, String returnType){
		super(name, name);
	}

	public MethodRecord getMethod(String name) {
		return methods.get(name);
	}

	public void setMethod(String name, MethodRecord method) {
		methods.put(name, method);
	}

	public Record getVariable(String Varname) {
		return variables.get(Varname);
	}

	public void setVariables(Map<String, Record> variables) {
		this.variables = variables;
	}
	
	@Override
	public String toString(){
		String result= "Class "+ this.name +"\nVariables: \n";
		for(Map.Entry<String, Record> v: variables.entrySet())
			result+=v.getValue().toString()+ "\n" ;
		result+="\nMethods:\n\t";
		for(Map.Entry<String,MethodRecord> m: methods.entrySet())
			result+=m.toString()+"\n\t";
		return result;
	}
}