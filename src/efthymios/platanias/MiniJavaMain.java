package efthymios.platanias;

import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import antlr.MJGrammarLexer;
import antlr.MJGrammarParser;

public class MiniJavaMain {

	public static void main(String[] args) throws Exception {
		
		String testProgram = "C:\\CC1testFiles\\factorial.java";
		System.out.println("Reading test program from: "+testProgram);
		
		try {
			ANTLRFileStream input = new ANTLRFileStream("C:\\CC1testFiles\\factorial.java");//ANTLRFileStream(args[0]);
			MJGrammarLexer lexer = new MJGrammarLexer(input);
			MJGrammarParser parser = new MJGrammarParser(new BufferedTokenStream(lexer));
			MJGrammarParser.ProgContext root = parser.prog();

			// Display tree
			Trees.inspect(root, parser);
					
			ParseTreeWalker walker = new ParseTreeWalker();
			SymbolTable table = new SymbolTable();
			walker.walk(new SymbolTableListener(table), root);
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			table.printTable();
			System.out.println("Done!");
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
