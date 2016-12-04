package efthymios.platanias;

public class Record {
	String name;
	String Type;
	
	public Record(String name, String Type){
		this.name = name;
		this.Type = Type;
	}
	
	public String getReturnType() {
		return Type;
	}
	public void setReturnType(String returnType) {
		this.Type = returnType;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString(){
		return Type+" "+name;
	}
	
}