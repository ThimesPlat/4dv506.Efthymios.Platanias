package efthymios.platanias;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MethodRecord extends Record{
	Map<String,Record> variables = new HashMap<String,Record>();
	Map<String,Record> parameters = new HashMap<String,Record>();
	
	public MethodRecord(String name, String returnType){
		super(name, returnType);	
	}
	
	public Record getVariables(String name) {
		return variables.get(name);
	}

	public void setVariables(String name, Record variable) {
		variables.put(name, variable);
	}
/*	
	public String toString(){
		String result= "Method: "+Type+" "+name+"\nVariables:";
		for(Map.Entry<String, Record> v: variables.entrySet()) 
			result+="\n\t"+v.getValue().toString();
		return result;
	}
*/
	public String toString(){
		String result= "Method: "+Type+" "+name+"\nParameters:";
		Collection params = getParameters();
		Iterator<Record> it = params.iterator();		
		while(it.hasNext()){
			result+="\n\t"+it.next().toString();
		}
		result +="\nVariables:";
		for(Map.Entry<String, Record> v: variables.entrySet()) 
			result+="\n\t"+v.getValue().toString();
		return result;
	}
	
	public List<Record> getParameters(){
		List<Record> result= new ArrayList<>();
		for(Record r:parameters.values()){
			result.add(r);
		}
		return result;
	}
	
	public void setParameter(String name, Record parameter) {
		parameters.put(name, parameter);
	}
}