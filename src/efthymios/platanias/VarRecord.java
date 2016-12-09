package efthymios.platanias;

public class VarRecord extends Record{
	
	public VarRecord(String name, String Type){
		super(name, Type);
	}
	
	@Override
	public String toString(){
		return Type+" "+name;
	}
}