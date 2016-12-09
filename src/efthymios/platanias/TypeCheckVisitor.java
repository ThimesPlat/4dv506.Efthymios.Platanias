package efthymios.platanias;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import antlr.MJGrammarBaseVisitor;
import antlr.MJGrammarParser.ArExprContext;
import antlr.MJGrammarParser.ArgContext;
import antlr.MJGrammarParser.ArrAssignStContext;
import antlr.MJGrammarParser.ArrIdExprContext;
import antlr.MJGrammarParser.AssignstContext;
import antlr.MJGrammarParser.BoolExprContext;
import antlr.MJGrammarParser.ClassDeclarationContext;
import antlr.MJGrammarParser.ExpressionContext;
import antlr.MJGrammarParser.IfSTContext;
import antlr.MJGrammarParser.InitExprContext;
import antlr.MJGrammarParser.MainClassContext;
import antlr.MJGrammarParser.MethodCallContext;
import antlr.MJGrammarParser.MethodContext;
import antlr.MJGrammarParser.PrintStContext;
import antlr.MJGrammarParser.PropertyContext;
import antlr.MJGrammarParser.ReturnStContext;
import antlr.MJGrammarParser.StringConcExprContext;
import antlr.MJGrammarParser.TypeContext;
import antlr.MJGrammarParser.WhileStContext;

public class TypeCheckVisitor extends MJGrammarBaseVisitor<String> {
	
	public SymbolTable table= new SymbolTable();
	
	public TypeCheckVisitor(SymbolTable table) {
		this.table=table;
	}
	
	//classDeclaration: 'class' ID LB fieldList methodList RB;
	@Override
	public String visitClassDeclaration(ClassDeclarationContext ctx){
		table.enterScope();		
		visit(ctx.getChild(4)); //visit methodList. 
		table.exitScope();
		return null;
	}
	
	//mainClass           : 	'class' ID LB mainMethod RB;
	@Override
	public String visitMainClass(MainClassContext ctx) {
		table.enterScope();
		visit(ctx.getChild(3));
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
	
	//returnSt: 'return' expression SC;
	@Override
	public String visitReturnSt(ReturnStContext ctx){
		return visit(ctx.getChild(1));
	}
	
	//printSt: 'System.out.println'LRB arg RRB SC;
	@Override 
	public String visitPrintSt(PrintStContext ctx) {
		String argType=visit(ctx.getChild(2));
		if (!(argType.equals("int")||argType.equals("String"))) throw new RuntimeException("Invalid argument in Print Statement");
		return null;
	}
	
	/*boolExpr : LRB boolExpr RRB
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
			else if(n.equals(ctx.COMP())) {
				String firstOpType=visit(ctx.getChild(0));    //|arExpr COMP arExpr
				String secondOpType=visit(ctx.getChild(2));   
				if(!((firstOpType.equals("int"))&&(secondOpType.equals("int")))) throw new RuntimeException("you can only compare integer types");
				return "boolean";
			}else if(n==ctx.EQ()){											//|arExpr EQ arExpr
				String firstOpType=visit(ctx.getChild(0));    
				String secondOpType=visit(ctx.getChild(2));  				
				if(!(((firstOpType.equals("int"))&&(secondOpType.equals("int")))||((firstOpType.equals("char"))&&(secondOpType.equals("char"))))) throw new RuntimeException("you can only use"
						+ "\"==\" operator on integer or character types");
				return "boolean";
			}else if(n==ctx.AND()||n==ctx.OR()){      //|boolExpr (AND|OR)boolExpr
				String firstOpType=visit(ctx.getChild(0));
				String secondOpType=visit(ctx.getChild(2));
				if(!(firstOpType.equals("boolean"))&&(secondOpType.equals("boolean"))) throw new RuntimeException("you can only use boolean operators on boolean expressions");
				return "boolean";
			}
		} else if (childrenNo == 2 ) {      //|NOT boolExpr
			String exprType=visit(ctx.getChild(1));
			if (!exprType.equals("boolean")) throw new RuntimeException("NOT operator works only with boolean expresssions");
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
		
	//property : ID('.'ID)+;
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
	
	//arrIdExpr : ID'['(INTEG|ID|property)']';
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
					if (!indexType.equals("int")) throw new RuntimeException("Array index must be an integer."+ varRec.getName()+" is not");					
				}
			}			
		} else {
			String indexType= visit(indexNode);
			if (!indexType.equals("int")) throw new RuntimeException("Array index must be an integer. Property"
					+ indexNode.getText()+ " is not");
			
		}
		return "int";
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
			if (childrenNo<=4){
				if(!(n instanceof TerminalNode)){
					methodName=ctx.getChild(childrenNo-4).getText();
					MethodRecord mRec=(MethodRecord) table.lookup(methodName);
					if (mRec==null)throw new RuntimeException("method "+methodName+ " is not declared");
					List<VarRecord> paramList=(List<VarRecord>) mRec.getParameters();
					List<ParseTree> arguments= new ArrayList<>();
					for(int i=0;i<=n.getChildCount();i+=2){
						arguments.add(n.getChild(i));
					}
					if(paramList.size()!=arguments.size())throw new RuntimeException("Incorrect number of arguments");
					else for(int i=0;i<=paramList.size()-1;i++){
						String actualType=visit(arguments.get(i));
						String declaredType=paramList.get(i).getReturnType();
						if(!actualType.equals(declaredType)) throw new RuntimeException("Incorrect argument type");
					}//checking arguments complete
					return mRec.getReturnType();
					} else {
						methodName=ctx.getChild(childrenNo-3).getText();
						ClassRecord cRec=(ClassRecord) table.lookup(methodName);
						MethodRecord mRec=cRec.getMethod(methodName);
						if (mRec==null)throw new RuntimeException("method "+methodName+ " is not declared");
						return mRec.getReturnType();
					}
			}else 
			if(!(n instanceof TerminalNode)){
				methodName=ctx.getChild(childrenNo-4).getText();
				//checking charAt(i) 
				if(methodName.equals("charAt")){
					if(n.getChildCount()!=1) throw new RuntimeException("Incorrect number of arguments on charAt()function");
					else {
						String charArgType=visit(n);		//check argument type is int
						if (!charArgType.equals("int")) throw new RuntimeException("ARgument i on function charAt(i) must be of type int");
					}
					String objectType=table.lookup(ctx.getChild(childrenNo-6).getText()).getReturnType();
					if (!objectType.equals("String")) throw new RuntimeException(".charAt(i) is applicable only to Strings");
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
				if(methodName.equals("length")){
					String objectType=table.lookup(ctx.getChild(childrenNo-5).getText()).getReturnType();
					if (!objectType.equals("String")) throw new RuntimeException(".length() is applicale only to Strings");
				}
				argTypes=null;
				for(int i=childrenNo-3;i>=0;i-=2){
					ids.push(ctx.getChild(i).getText());
				}
			}
			int count=ids.size();
			ClassRecord cRec=null;
			Record r;
			for(int i=0;i<=count-2;i++){
				String key=ids.pop();
				r= table.lookup(key);				
				cRec=(ClassRecord)table.lookup(r.Type);
				if (cRec==null) throw new RuntimeException("Class "+key+" is not declared");				
			}
			//last item in stack is the method Identifier
			MethodRecord mRec=cRec.getMethod(ids.pop());
			if (mRec==null)throw new RuntimeException("Method not declared in class "+cRec.getName());
			returnType=mRec.getReturnType();
			if(methodName.equals("charAt")) returnType="char";
			if(methodName.equals("length"))returnType="int";
			//checking arguments 
			if(argTypes==null) return returnType;
			else {				
				List<VarRecord> parameters=(List<VarRecord>) mRec.getParameters();
				if(parameters.size()!=argTypes.size()) throw new RuntimeException("Incorect number of arguments on method call");
				else{
					for(int i=0;i<argTypes.size();i++){
						if(!argTypes.get(i).equals((parameters.get(i).getReturnType()))) throw new RuntimeException ("incorrect argument type");
					}
				}
			} return returnType;
		}	else 						// last node is a RuleNode
			{
				if (childrenNo==3){					
				String leftType=visit(ctx.getChild(0)); //also checks the first method for undeclared ids
				//ClassRecord cRec=(ClassRecord) table.lookup(ctx.getChild(0).getText());
				ParseTree node=ctx.getChild(2);
				//checking charAt function
				if((node.getChildCount()==4)&&(node.getChild(3) instanceof TerminalNode)){			
					if (node.getChild(0).getText().equals("charAt")){
						if (!leftType.equals("String")) throw new RuntimeException("charAt() only applicable to Strings");
						String argType=visit(node.getChild(2));
						if (!argType.equals("int")) throw new RuntimeException("Incorrect argument in charAt()");
						return "char";
					}
				}else if(node.getChildCount()==3&&((node.getChild(2) instanceof TerminalNode))){
					if(node.getChild(0).getText().equals("length")){
						if (!leftType.equals("String")) throw new RuntimeException("length() only applicable to Strings");
						else return "int";
					}
				}
				{String mName=ctx.getChild(2).getChild(0).getText();
				ClassRecord tempCR=(ClassRecord) table.lookup(ctx.getChild(0).getChild(0).getText());
				MethodRecord mRec=tempCR.getMethod(mName);
				if(mRec==null)throw new RuntimeException("method "+mName+" is not declared in class "+tempCR.getName());
				return mRec.getReturnType(); //visit the last methodCall in the chain 
				}
				}
				else if (childrenNo==5){
					String leftType=visit(ctx.getChild(1)); //also checks the first method for undeclared ids
					ParseTree node=ctx.getChild(4);
					//checking charAt function
					if((node.getChildCount()==4)&&(node.getChild(3) instanceof TerminalNode)){			
						if (node.getChild(0).getText().equals("charAt")){
							if (!leftType.equals("String")) throw new RuntimeException("charAt() only applicable to Strings");
							else return visit(node);
						}
					}else if(node.getChildCount()==3&&((node.getChild(2) instanceof TerminalNode))){
						if(node.getChild(0).getText().equals("length")){
							if (!leftType.equals("String")) throw new RuntimeException("length() only applicable to Strings");
							else return visit(node);
						}
					}
					return visit(node); //visit the last methodCall in the chain 
				}
			}
		return null; //debuggin purposes. should be unreachable
	}
	
	/*expression : LRB expression RRB | ID | property | STRING | BOOLEANLIT | stringConcExpr
				  | initExpr | methodCall | arrIdExpr | boolExpr | arExpr;*/
	@Override
	public String visitExpression(ExpressionContext ctx) {
		if(ctx.getChildCount() == 3){
			return visit(ctx.getChild(1));
		}
		ParseTree cur = ctx.getChild(0);
		if(!(cur instanceof TerminalNode)){
			return visit(cur);
		}
		else if(cur == ctx.ID()){
			String key=visitTerminal((TerminalNode)cur);
			Record id= table.lookup(key);
			if (id==null) throw new RuntimeException("Identifier "+key+" is not declared");					
			return id.getReturnType();
		}
		else if (cur==ctx.BOOLEANLIT()) return "boolean";
		else if (cur==ctx.STRING()) return "String";
		return null;
	}

	//arrAssignSt : ID'['(INTEG|ID|property)']'ASSIGNOP expression SC;
	@Override
	public String visitArrAssignSt(ArrAssignStContext ctx) {
		String key=visitTerminal((TerminalNode)ctx.getChild(0));
		Record id= table.lookup(key);
		if (id==null) throw new RuntimeException("Identifier "+key+" is not declared");	
		if (!id.getReturnType().equals("int[]")) throw new RuntimeException("Identifier "+key+" is not int[]");		
		ParseTree cur = ctx.getChild(2);
		String temp = null;
		if(cur == ctx.INTEG()){
			temp = "int";
		}
		else if(cur == ctx.ID(1)){
			key=visitTerminal((TerminalNode)cur);
			id= table.lookup(key);
			if (id==null) throw new RuntimeException("Identifier "+key+" is not declared");					
			temp = id.getReturnType();
		}
		else{
			temp = visit(cur);
		}
		if(!temp.equals("int"))throw new RuntimeException("Only type int inside ID[]");
		if(!visit(ctx.getChild(5)).equals("int"))throw new RuntimeException("Only type int assigned on ID[]");
		return("int");
	}

	//arg :	ID|CH|STRING|expression|methodCall;	
	@Override
	public String visitArg(ArgContext ctx) {
		String returnType = null;
		ParseTree cur = ctx.getChild(0);
		if(cur == ctx.ID()){
			String key=visitTerminal((TerminalNode)cur);
			Record id= table.lookup(key);
			if (id==null) throw new RuntimeException("Identifier "+key+" is not declared");					
			return id.getReturnType();
		}
		else if(cur == ctx.CH()){
			return ("char");
		}
		else if(cur == ctx.STRING()){
			return ("String");
		}
		else{
			returnType = visit(cur);
		}
		return returnType;
	}

	
	//stringConcExpr : (STRING|ID|property)PLUS(STRING|ID|property);
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

	//assignst : (ID|property) ASSIGNOP expression SC;
	@Override
	public String visitAssignst(AssignstContext ctx) {
		ParseTree cur = ctx.getChild(0);
		String left = null;
		String right = null;
		if(cur == ctx.ID()){
			String key=visitTerminal((TerminalNode)cur);
			Record id= table.lookup(key);
			if (id==null) throw new RuntimeException("Identifier "+key+" is not declared");					
			left = id.getReturnType();
		}
		else {left = visit(cur);}
		right = visit(ctx.getChild(2));
		if(right.equals(left)){			
			return right;			
		}
		else throw new RuntimeException("Not the same type in left and right of Assign");
	}
	
	//initExpr			:	 'new' (methodCall|type) ;
	@Override
	public String visitInitExpr(InitExprContext ctx) {
		return visit(ctx.getChild(1));
	}
	
	
	//type	: 	ID|'int'|'String'|'char'|'boolean'|'int' '['(INTEG|ID)?']';
	@Override
	public String visitType(TypeContext ctx) {
		int childrenNo=ctx.getChildCount();
		if (childrenNo>1){
			ParseTree n=ctx.getChild(2);
			if(n.getText().equals("]"))return "int[]";
			else if(n == ctx.INTEG()) return "int[]";
			else if(n==ctx.ID()){
				Record id=table.lookup(visitTerminal((TerminalNode) n));
				if (id==null) throw new RuntimeException("Unidentified identifier "+n.getText()+" in array init expression");
				else if(!id.getReturnType().equals("int")) throw new RuntimeException("Wrong type in array init expression");
				else return "int[]";
			}
				
		}else {
		 ParseTree node= ctx.getChild(0);
		 if (node==ctx.ID()){
			 Record id=table.lookup(visitTerminal((TerminalNode) node));
				if (id==null) throw new RuntimeException("Undeclared identifier "+node.getText());
				return id.getReturnType();
		 } else {
			 return visitTerminal((TerminalNode)ctx.getChild(0));
		 }
		}
		return null; //debug
	}

	/*arExpr :	LRB arExpr RRB 
			   	|arExpr(MULT|DIV)arExpr
			   	|arExpr(PLUS|MINUS)arExpr
				|(INTEG|ID|property|CH|arrIdExpr|methodCall);*/
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

	// whileSt : 'while'LRB boolExpr RRB stmntBlock;
	@Override 
	public String visitWhileSt(WhileStContext ctx){
		String exprType=visit(ctx.getChild(2));
		if(!(exprType.equals("boolean"))) throw new RuntimeException("expression in while statement must be of type boolean");
		visit(ctx.getChild(4));
		return null;
	}
	
	//ifST : 'if'LRB boolExpr RRB statement ('else' statement)?;
		@Override
		public String visitIfST(IfSTContext ctx) {
			String type = visit(ctx.getChild(2));
			if(!type.equals("boolean")) throw new RuntimeException("Expecting type Boolean in If "+ctx.getChild(2)+" is not boolean");
			visit(ctx.getChild(4));
			if(ctx.getChildCount()>5){
				visit(ctx.getChild(6));
			}
			return null;
		}

	
	@Override 
	public String visitTerminal(TerminalNode node){			
		System.out.println(node.getSymbol().getText());
		return node.getSymbol().getText();
	}
}