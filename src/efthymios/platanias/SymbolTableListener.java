package efthymios.platanias;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;

import antlr.MJGrammarBaseListener;
import antlr.MJGrammarParser.ClassDeclarationContext;
import antlr.MJGrammarParser.MainClassContext;
import antlr.MJGrammarParser.MethodContext;
 
public class SymbolTableListener extends MJGrammarBaseListener {
 
    public SymbolTable table;
    private ClassRecord lastClassRecord;
    //remembers last entered class
    
    public SymbolTableListener(SymbolTable table){
    	this.table = table; 
    }
    
    @Override
	public void enterMainClass(MainClassContext ctx) {
    	// mainClass : 'class' ID LB mainMethod RB;
        ClassRecord classRecord = new ClassRecord(ctx.getChild(1).getText(), ctx.getChild(1).getText());
        table.put(ctx.getChild(1).getText(), classRecord);  //Place the record of the class into the current scope
        table.enterScope(); //Enter class scope (create new child of current scope)
        Scope classScope = table.getCurrentScope(); //Get entered scope which was created and is current now
        classScope.setName("mainclass");   //Set name/key of the created scope
        //classScope.printScope();
        lastClassRecord = classRecord;
        // Declare "this" inside the scope
        table.put("this", new Record("this", ctx.getChild(1).getText()));
	}
    
	@Override
	public void enterClassDeclaration(ClassDeclarationContext ctx) {		
		Map<String,Record> variables = new HashMap<String,Record>();
		Map<String,MethodRecord> methods = new HashMap<String,MethodRecord>();	
		// classDeclaration : 'class' ID LB fieldList methodList RB;
        ClassRecord classRecord = new ClassRecord(ctx.getChild(1).getText(), ctx.getChild(1).getText());
        // Putting variables inside classRecord
        // fieldList : (field)* ;
        // field : type ID SC;
        for(int i=0; i<ctx.getChild(3).getChildCount(); i+=1){
        	String varName = ctx.getChild(3).getChild(i).getChild(1).getText();
        	String varType = ctx.getChild(3).getChild(i).getChild(0).getText();
        	if(variables.containsKey(varName)){ System.err.println(varType + "\t already exist");}
        	else
        		variables.put(varName, new Record(varName, varType));
        }
        classRecord.setVariables(variables);
        // Putting methods inside classRecord        
        for(int i=0; i<ctx.getChild(4).getChildCount(); i++){
        	ParseTree curMethod = ctx.getChild(4).getChild(i);
        	String methName = curMethod.getChild(1).getText();
        	String methType = curMethod.getChild(0).getChild(0).getText();
           	MethodRecord methodRecord = new MethodRecord(methName, methType);
           	// paramList:   (type ID(','type ID)*)? ;            
   	     	for (int k = 0; k < curMethod.getChild(3).getChildCount(); k += 3) {
   	     		Record rec = new Record(curMethod.getChild(3).getChild(k + 1).getText(), curMethod.getChild(3).getChild(k).getChild(0).getText());
   	     		if(methodRecord.getParameters().contains(curMethod.getChild(3).getChild(k + 1).getText()))
   	     			System.err.println(curMethod.getChild(3).getChild(k + 1).getText() + "\t already exist");
   	     		else
   	     			methodRecord.setParameter(curMethod.getChild(3).getChild(k + 1).getText(),rec);
   	     	}
        	methods.put(methName, methodRecord);
        }
        
        classRecord.setMethods(methods);
        //Place the record of the class into the current scope
        table.put(ctx.getChild(1).getText(), classRecord);
        //Enter class scope (create new child of current scope)
        table.enterScope();
        //Get entered scope which was created and is current now
        Scope classScope = table.getCurrentScope();
        //Set name of the created scope
        classScope.setName("class " + ctx.getChild(1).getText());
        //Set the created scope as personal scope of the class entry
        lastClassRecord = classRecord;
        // Declare "this" inside the scope
        //table.put("this", new Record("this", ctx.getChild(1).getText()));
        table.put("this", classRecord );
        // table.printTable();
	}

	@Override
	public void enterMethod(MethodContext ctx) {
		 // method : type ID LRB paramList RRB LB fieldList statementList (returnSt)?RB;
	     MethodRecord methodRecord = new MethodRecord(ctx.getChild(1).getText(), ctx.getChild(0).getChild(0).getText());
	     // paramList:   (type ID(','type ID)*)? ;
	     for (int i = 0; i < ctx.getChild(3).getChildCount(); i += 3) {
	    	Record rec = new Record(ctx.getChild(3).getChild(i + 1).getText(), ctx.getChild(3).getChild(i).getChild(0).getText());
	    	if(methodRecord.getParameters().contains(ctx.getChild(3).getChild(i + 1).getText()) || 
	    	  (methodRecord.getVariables(ctx.getChild(3).getChild(i + 1).getText())!=null) )
	    		System.err.println(ctx.getChild(3).getChild(i + 1).getText() + "\t already exist");
	     	else
	     		methodRecord.setParameter(ctx.getChild(3).getChild(i + 1).getText(),rec);
	     }
	     // fieldList : (field)* ;
	     for (int i = 0; i < ctx.getChild(6).getChildCount(); i++) {
	    	 ParseTree curField = ctx.getChild(6).getChild(i);
	    	 String name = curField.getChild(1).getText(); // name of field 
	    	 String returnType = curField.getChild(0).getChild(0).getText();  // return type of field
	    	 Record rec = new Record(name,returnType );
	    	 if( methodRecord.getVariables(name)!=null ||
	    	     methodRecord.getParameters().contains(name))
		    		System.err.println(name + "\t already exist");
		     else
		    	 methodRecord.setVariables(name,rec);
	     }
	     table.put(ctx.getChild(1).getText(), methodRecord);
	     //Enter method scope (create new child of current scope)
	     table.enterScope();
	     //Get entered scope which was created and is current now
	     Scope methodScope = table.getCurrentScope();
	     //Set name of the created scope
	     methodScope.setName("method " + ctx.getChild(1).getText());
	}	
}