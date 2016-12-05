package efthymios.platanias;

import antlr.MJGrammarBaseVisitor;
import antlr.MJGrammarParser.*;

public class TypeCheckVisitor<String> extends MJGrammarBaseVisitor<String> {
	
	SymbolTable table= new SymbolTable();
	
	//classDeclaration: 'class' ID LB fieldList methodList RB;
	@Override
	public String visitClassDeclaration(ClassDeclarationContext ctx){
		table.enterScope();		
		visit(ctx.getChild(4)); //visit methodList. 
		table.exitScope();
		return null;
	}
	
	//method: type ID LRB paramList RRB LB fieldList statementList (returnSt)?RB;
	
	@Override 
	public String visitMethod(MethodContext ctx){
		table.enterScope();
		String declaredRetType=visit(ctx.getChild(0));
		visit(ctx.getChild(7));		
		String retType=visit(ctx.getChild(8));
		if(!declaredRetType.equals(retType))throw new RuntimeException("Method must return "+ declaredRetType);
		table.exitScope();
		return declaredRetType;
	}
	
	//returnSt: 	'return' expression SC;
	@Override
	public String visitReturnSt(ReturnStContext ctx){
		return visit(ctx.getChild(1));
	}
	
	//printSt:'System.out.println'LRB arg RRB SC;
	@Override 
	public String visitPrintSt(PrintStContext ctx) {
		String argType=visit(ctx.getChild(2));
		if (!(argType.equals("INTEG")||argType.equals("STRING"))) throw new RuntimeException("Invalid argument in Print Statement");
		return null;
	}
	
	public String visitBoolExpr(BoolExprContext ctx) {
		
	}
	
	

}
