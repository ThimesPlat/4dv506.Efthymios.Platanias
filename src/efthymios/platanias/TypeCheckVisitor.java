package efthymios.platanias;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import antlr.MJGrammarBaseVisitor;
import antlr.MJGrammarParser.ArrIdExprContext;
import antlr.MJGrammarParser.BoolExprContext;
import antlr.MJGrammarParser.ClassDeclarationContext;
import antlr.MJGrammarParser.MethodCallContext;
import antlr.MJGrammarParser.MethodContext;
import antlr.MJGrammarParser.PrintStContext;
import antlr.MJGrammarParser.PropertyContext;
import antlr.MJGrammarParser.ReturnStContext;

public class TypeCheckVisitor extends MJGrammarBaseVisitor<String> {
	
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
	
	/*boolExpr			:	LRB boolExpr RRB
							|arExpr COMP arExpr
							|arExpr EQ arExpr 
							|boolExpr (AND|OR)boolExpr
							|NOT boolExpr
							|(ID|property|BOOLEANLIT|arrIdExpr|methodCall) ;
	*/
	@Override
	public String visitBoolExpr(BoolExprContext ctx) {
		int childrenNo=ctx.children.size();
		if (childrenNo == 3 )
		{
			ParseTree n=ctx.getChild(1);			
			if (!(n instanceof TerminalNode)) return visit(n);    //( boolExpr ) 
			else if(n == ctx.EQ()) {
				String firstOpType=visit(ctx.getChild(0));    //|arExpr COMP arExpr
				String secondOpType=visit(ctx.getChild(2));   
				if(!((firstOpType=="int")&&(secondOpType=="int"))) throw new RuntimeException("you can only compare integer types");
				return "boolean";
			}else if(n==ctx.EQ()){											//|arExpr EQ arExpr
				String firstOpType=visit(ctx.getChild(0));    
				String secondOpType=visit(ctx.getChild(2));  				
				if(!(((firstOpType=="int")&&(secondOpType=="int"))||((firstOpType=="char")&&(secondOpType=="char")))) throw new RuntimeException("you can only use"
						+ "\"==\" operator on integer or character types");
				return "boolean";
			}else if(n==ctx.AND()||n==ctx.OR()){      //|boolExpr (AND|OR)boolExpr
				String firstOpType=visit(ctx.getChild(0));
				String secondOpType=visit(ctx.getChild(2));
				if(!(firstOpType=="boolean")&&(secondOpType=="boolean")) throw new RuntimeException("you can only use boolean operators on boolean expressions");
				return "boolean";
			}
		} else if (childrenNo == 2 ) {      //|NOT boolExpr
			String exprType=visit(ctx.getChild(1));
			if (exprType!="boolean") throw new RuntimeException("NOT operator works only with boolean expresssions");
				return "boolean";
		}else  {								//|(ID|property|BOOLEANLIT|arrIdExpr|methodCall)
			ParseTree n=ctx.getChild(0);
			if (n instanceof TerminalNode) {				
				if (n==ctx.BOOLEANLIT()) return "boolean";
				else if(n==ctx.ID()){
					String key=visitTerminal((TerminalNode)n);
					Record id= table.lookup(key);
					if (id==null) throw new RuntimeException("Identifier "+key+" is not declared");					
					return id.getReturnType();
					
				}			 
			}else {
				String type=visit(ctx.getChild(0));
				return type;
			}
			
		}
		return null; //for debug
	}
	
	
	//property			: 	ID('.'ID)+;
	@Override
	public String visitProperty(PropertyContext ctx){
		int childrenNo=ctx.getChildCount();
		String classKey=visitTerminal((TerminalNode)ctx.getChild(0));
		ClassRecord cRec= (ClassRecord) table.lookup(classKey);
		if (cRec==null) throw new RuntimeException("Class does not exist in propery statement");
		ClassRecord childClass;
		for (int i=2;i<=childrenNo;i+=2){
			String varName=visitTerminal((TerminalNode)ctx.getChild(i));
			if (i<childrenNo) {
				childClass=(ClassRecord) table.lookup(varName);
				if (childClass==null) throw new RuntimeException(varName+" used in  "+ cRec.getName()+ " is not declared");
			}else 
				if(i==childrenNo) {
					Record varRec=childClass.getVariable(varName);
					if (varRec==null)  throw new RuntimeException("variable "+ varName+" used in  "+ cRec.getName()+ " is not declared");
					else return varRec.getReturnType();
				}
		}
		return null; //debugging purposes, normally unreachable
	}

	
	//arrIdExpr			: 	ID'['(INTEG|ID|property)']';
	@Override
	public String visitArrIdExpr(ArrIdExprContext ctx) {
		String arrKey=visitTerminal((TerminalNode)ctx.getChild(0));
		Record array=table.lookup(arrKey);
		if (array==null) throw new RuntimeException("Array "+arrKey+" is not declared");
		ParseTree indexNode=ctx.getChild(2);
		if (indexNode instanceof TerminalNode) {
			if (indexNode!=ctx.INTEG()){
				if(indexNode==ctx.ID()) {
					Record varRec= table.lookup(visitTerminal((TerminalNode)indexNode));
					if (varRec==null)throw new RuntimeException("Variable "+indexNode.getText()+" is not declared");
					String indexType= varRec.getReturnType();
					if (indexType!="int") throw new RuntimeException("Array index must be an integer."+ varRec.getName()+" is not");					
				}
			}
			
		} else {
			String indexType= visit(indexNode);
			if (indexType!="int") throw new RuntimeException("Array index must be an integer. Property"
					+ indexNode.getText()+ " is not");
			
		}
		return array.getReturnType();
	}
	
	
	/*methodCall			: 	(ID'.')* ID LRB (argument)?RRB
           				 |(LRB methodCall RRB)'.'methodCall
           				 |methodCall '.' methodCall; 
           				 */
	@Override
	public String visitMethodCall(MethodCallContext ctx) {
		int childrenNo=ctx.children.size();
		ParseTree lastChild=ctx.children.get(childrenNo-1);
		if(lastChild instanceof TerminalNode){
			ParseTree n=ctx.children.get(childrenNo-2);
			if(!(n instanceof TerminalNode)){
				List<String> argTypes=new ArrayList<>();
				for(int i=0;i<=n.getChildCount();i+=2){
					argTypes.add(visit(n.getChild(i)));
				}
				
			}
		}
	}
	
	@Override 
	public String visitTerminal(TerminalNode node){		
		return node.getSymbol().getText();
	}
	
	

}
