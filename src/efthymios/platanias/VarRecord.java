package efthymios.platanias;

public class VarRecord extends Record {
	
	private String type;

	public VarRecord(String n, String t) {
		super(n);
		type=t;
		
	}
	
	public String getType(){return type;}

}
