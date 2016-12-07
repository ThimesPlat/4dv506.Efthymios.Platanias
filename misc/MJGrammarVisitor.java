// Generated from MJGrammar.g4 by ANTLR 4.5.3

	package antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MJGrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MJGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(MJGrammarParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#mainClass}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMainClass(MJGrammarParser.MainClassContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#mainMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMainMethod(MJGrammarParser.MainMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#classDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDeclaration(MJGrammarParser.ClassDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#fieldList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldList(MJGrammarParser.FieldListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#methodList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodList(MJGrammarParser.MethodListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField(MJGrammarParser.FieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(MJGrammarParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#printSt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrintSt(MJGrammarParser.PrintStContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#continueSt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueSt(MJGrammarParser.ContinueStContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#breakSt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakSt(MJGrammarParser.BreakStContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#stmntBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmntBlock(MJGrammarParser.StmntBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#assignst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignst(MJGrammarParser.AssignstContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#arrAssignSt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrAssignSt(MJGrammarParser.ArrAssignStContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#ifST}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfST(MJGrammarParser.IfSTContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#returnSt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnSt(MJGrammarParser.ReturnStContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#whileSt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileSt(MJGrammarParser.WhileStContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument(MJGrammarParser.ArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#arg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArg(MJGrammarParser.ArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(MJGrammarParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(MJGrammarParser.MethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#methodCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodCall(MJGrammarParser.MethodCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#initExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitExpr(MJGrammarParser.InitExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#arrIdExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrIdExpr(MJGrammarParser.ArrIdExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#stringConcExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringConcExpr(MJGrammarParser.StringConcExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#arExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArExpr(MJGrammarParser.ArExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolExpr(MJGrammarParser.BoolExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(MJGrammarParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MJGrammarParser#property}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProperty(MJGrammarParser.PropertyContext ctx);
}