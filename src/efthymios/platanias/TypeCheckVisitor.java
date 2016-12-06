package efthymios.platanias;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import antlr.MJGrammarBaseVisitor;
import antlr.MJGrammarParser.BoolExprContext;
import antlr.MJGrammarParser.ClassDeclarationContext;
import antlr.MJGrammarParser.MethodContext;
import antlr.MJGrammarParser.PrintStContext;
import antlr.MJGrammarParser.ReturnStContext;

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
	
	@Override
	public String visitBoolExpr(BoolExprContext ctx) {
		int childrenNo=ctx.children.size();
		if (childrenNo == 3 )
		{
			ParseTree n=ctx.getChild(1);			
			if (!(n instanceof TerminalNode)) visit(n);    //( boolExpr ) 
			else if(n == ctx.COMP()||n == ctx.EQ()) {
				String firstOpType=visit(ctx.getChild(0));
				String secondOpType=visit(ctx.getChild(2));
				if(!(firstOpType=="int")&&(secondOpType=="int")) throw new RuntimeException("you can only compare integer types");
			}else if(n==ctx.AND()||n==ctx.OR()){
				String firstOpType=visit(ctx.getChild(0));
				String secondOpType=visit(ctx.getChild(2));
				if(!(firstOpType=="boolean")&&(secondOpType=="boolean")) throw new RuntimeException("you can only use boolean operators on boolean expressions");
			}
		} else if (childrenNo == 2 ) {
			String exprType=visit(ctx.getChild(1));
			if (exprType!="boolean") throw new RuntimeException("NOT operator works only with boolean expresssions");
			
		}else  {
			ParseTree 
		}
	}
	
	@Override 
	public String visitTerminal(TerminalNode node){
		return (String) node.getSymbol().getText();
	}
	
	

}
