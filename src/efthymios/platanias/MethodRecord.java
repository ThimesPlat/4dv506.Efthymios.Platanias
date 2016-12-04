package efthymios.platanias;

import java.util.HashMap;
import java.util.Map;

public class MethodRecord extends Record{
	Map<String,Record> variables = new HashMap<String,Record>();
	
	public MethodRecord(String name, String returnType){
		super(name, returnType);	
	}
	
	public Record getVariables(String name) {
		return variables.get(name);
	}

	public void setVariables(String name, Record variable) {
		variables.put(name, variable);
	}
	
	public String toString(){
		String result= "Method: "+Type+" "+name+"\nVariables:";
		for(Map.Entry<String, Record> v: variables.entrySet()) 
			result+="\n\t"+v.getValue().toString();
		return result;
	}
}