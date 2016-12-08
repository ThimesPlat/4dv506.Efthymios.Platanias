package efthymios.platanias;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import antlr.MJGrammarBaseVisitor;
import antlr.MJGrammarParser.ArExprContext;
import antlr.MJGrammarParser.ArrIdExprContext;
import antlr.MJGrammarParser.BoolExprContext;
import antlr.MJGrammarParser.ClassDeclarationContext;
import antlr.MJGrammarParser.MethodCallContext;
import antlr.MJGrammarParser.MethodContext;
import antlr.MJGrammarParser.PrintStContext;
import antlr.MJGrammarParser.PropertyContext;
import antlr.MJGrammarParser.ReturnStContext;
import antlr.MJGrammarParser.StringConcExprContext;

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
		ClassRecord childClass=null;
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
				if(indexNode==ctx.ID(1)) { //Check if index is correct (assuming 1=2)
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
		String returnType;
		String methodName;
		if(lastChild instanceof TerminalNode){
			List<String> argTypes=new ArrayList<>();
			Stack<String> ids=new Stack<>();
			ParseTree n=ctx.children.get(childrenNo-2);
			if(!(n instanceof TerminalNode)){
				methodName=ctx.getChild(childrenNo-4).getText();
				//checking charAt(i) 
				if(methodName=="charAt"){
					if(n.getChildCount()!=1) throw new RuntimeException("Incorrect number of arguments on charAt()function");
					else {
						String charArgType=visit(n);		//check argument type is int
						if (charArgType!="int") throw new RuntimeException("ARgument i on function charAt(i) must be of type int");
					}
					String objectType=table.lookup(ctx.getChild(childrenNo-6).getText()).getReturnType();
					if (objectType!="String") throw new RuntimeException(".charAt(i) is applicale only to Strings");
				}
				for(int i=0;i<=n.getChildCount();i+=2){
					argTypes.add(visit(n.getChild(i)));
				}
			for(int i=childrenNo-4;i>=0;i-=2){
				ids.push(ctx.getChild(i).getText());
			}
			}else{ 
				//checking .length()
				methodName=ctx.getChild(childrenNo-3).getText();
				if(methodName=="length"){
					String objectType=table.lookup(ctx.getChild(childrenNo-5).getText()).getReturnType();
					if (objectType!="String") throw new RuntimeException(".length() is applicale only to Strings");
				}
				argTypes=null;
				for(int i=childrenNo-3;i>=0;i-=2){
					ids.push(ctx.getChild(i).getText());
				}
			}
			int count=ids.size();
			ClassRecord cRec=null;
			for(int i=0;i<=count-1;i++){
				String key=ids.pop();
				cRec=(ClassRecord) table.lookup(key);
				
				if (cRec==null) throw new RuntimeException("Class "+key+" is not declared");				
			}
			//last item in stack is the method Identifier
			MethodRecord mRec=cRec.getMethod(ids.pop());
			if (mRec==null)throw new RuntimeException("Method not declared in class "+cRec.getName());
			returnType=mRec.getReturnType();
			if(methodName=="charAt") returnType="char";
			if(methodName=="length")returnType="int";
			//checking arguments 
			if(argTypes==null) return returnType;
			else {				
				List<Record> parameters=(List<Record>) mRec.getParameters();
				if(parameters.size()!=argTypes.size()) throw new RuntimeException("Incorect number of arguments on method call");
				else{
					for(int i=0;i<=argTypes.size();i++){
						if(argTypes.get(i)!=(parameters.get(i).getReturnType())) throw new RuntimeException ("incorrect argument type");
					}
				}
			} return returnType;
		}	else 
			{
				if (childrenNo==3){			
				String leftType=visit(ctx.getChild(0)); //also checks the first method for undeclared ids
				ParseTree node=ctx.getChild(2);
				//checking charAt function
				if((node.getChildCount()==4)&&(node.getChild(3) instanceof TerminalNode)){			
					if (node.getChild(0).getText()=="charAt"){
						if (leftType!="String") throw new RuntimeException("charAt() only applicable to Strings");
						else return visit(node);
					}
				}else if(node.getChildCount()==3&&((node.getChild(2) instanceof TerminalNode))){
					if(node.getChild(0).getText()=="length"){
						if (leftType!="String") throw new RuntimeException("length() only applicable to Strings");
						else return visit(node);
					}
				}
				return visit(node); //visit the last methodCall in the chain 
				}
				else if (childrenNo==4){
					String leftType=visit(ctx.getChild(1)); //also checks the first method for undeclared ids
					ParseTree node=ctx.getChild(4);
					//checking charAt function
					if((node.getChildCount()==4)&&(node.getChild(3) instanceof TerminalNode)){			
						if (node.getChild(0).getText()=="charAt"){
							if (leftType!="String") throw new RuntimeException("charAt() only applicable to Strings");
							else return visit(node);
						}
					}else if(node.getChildCount()==3&&((node.getChild(2) instanceof TerminalNode))){
						if(node.getChild(0).getText()=="length"){
							if (leftType!="String") throw new RuntimeException("length() only applicable to Strings");
							else return visit(node);
						}
					}
					return visit(node); //visit the last methodCall in the chain 
				}
			}
		return null; //debuggin purposes. should be unreachable
	}
	
	
	@Override
	public String visitStringConcExpr(StringConcExprContext ctx) {
		String left = null;
		String right = null;
		ParseTree cl = ctx.getChild(0);
		ParseTree cr = ctx.getChild(2);
		if(cl ==ctx.STRING()){
			left = "String";
		}
		else if(cl == ctx.ID()){
			String key=visitTerminal((TerminalNode)cl);
			Record id= table.lookup(key);
			if (id==null) throw new RuntimeException("Identifier "+key+" is not declared");					
			left = id.getReturnType();					
		}
		else if(!(cl instanceof TerminalNode)){
			left = visit(cl);
		}
		if(cr ==ctx.STRING()){
			right = "String";
		}
		else if(cr == ctx.ID()){
			String key=visitTerminal((TerminalNode)cl);
			Record id= table.lookup(key);
			if (id==null) throw new RuntimeException("Identifier "+key+" is not declared");					
			right = id.getReturnType();					
		}
		else if(!(cr instanceof TerminalNode)){
			right = visit(cr);
		}
		if(!(right.equals(left) && right.equals("String")) ){throw new RuntimeException("String Concatenation works only with Strings");}
		return("String");
	}

	@Override
	public String visitArExpr(ArExprContext ctx) {
		int chNo=ctx.children.size();
		if(chNo == 3){
			ParseTree cur = ctx.getChild(1);
			if(cur==ctx.MULT() || cur==ctx.DIV()|| cur==ctx.PLUS() || cur==ctx.MINUS()){
				String left = visit(ctx.getChild(0));
				String right = visit(ctx.getChild(2));
				if(!(right.equals(left) && right.equals("int")) ){throw new RuntimeException("Arithmetic operations only with integers");}
				return("int");
			}
			else{
				return(visit(cur));
			}
		}
		else{
			ParseTree cur=ctx.getChild(0);
			if (cur instanceof TerminalNode) {				
				if (cur==ctx.INTEG()) return "int";
				else if (cur==ctx.CH()) return "char";
				else if(cur==ctx.ID()){
					String key=visitTerminal((TerminalNode)cur);
					Record id= table.lookup(key);
					if (id==null) throw new RuntimeException("Identifier "+key+" is not declared");					
					return id.getReturnType();					
				}			 
			}else {
				String type=visit(ctx.getChild(0));
				return type;
			}
			
		}
		return null;
	}

	@Override 
	public String visitTerminal(TerminalNode node){		
		return node.getSymbol().getText();
	}
	
	

}
