package efthymios.platanias;

public abstract class Record {
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
	

	
}