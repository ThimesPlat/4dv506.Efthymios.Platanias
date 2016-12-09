package efthymios.platanias;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MethodRecord extends Record{
	Map<String,VarRecord> variables = new HashMap<String,VarRecord>();
	Map<String,VarRecord> parameters = new HashMap<String,VarRecord>();
	
	public MethodRecord(String name, String returnType){
		super(name, returnType);	
	}
	
	public VarRecord getVariables(String name) {
		return variables.get(name);
	}

	public void setVariables(String name, VarRecord variable) {
		variables.put(name, variable);
	}

	public String toString(){
		String result= "Method: "+Type+" "+name+"\nParameters:";
		Collection params = getParameters();
		Iterator<VarRecord> it = params.iterator();		
		while(it.hasNext()){
			result+="\n"+it.next().toString();
		}
		result +="\nVariables:";
		for(Map.Entry<String, VarRecord> v: variables.entrySet()) 
			result+="\n"+v.getValue().toString();
		result+="\n_______________\n";
		return result;
	}
	
	public List<VarRecord> getParameters(){
		List<VarRecord> result= new ArrayList<>();
		for(VarRecord r:parameters.values()){
			result.add(r);
		}
		return result;
	}
	
	public void setParameter(String name, VarRecord parameter) {
		parameters.put(name, parameter);
	}
}