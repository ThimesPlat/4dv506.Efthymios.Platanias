package efthymios.platanias;

import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.BufferedTokenStream;

import antlr.MJGrammarLexer;
import antlr.MJGrammarParser;

public class MiniJavaMain {

	public static void main(String[] args) throws Exception {
		
		System.out.println("Reading test program from: ");//+args[0]);
		
		try {
			ANTLRFileStream input = new ANTLRFileStream("C:\\quicksort.java");//ANTLRFileStream(args[0]);
			MJGrammarLexer lexer = new MJGrammarLexer(input);
			MJGrammarParser parser = new MJGrammarParser(new BufferedTokenStream(lexer));
			MJGrammarParser.ProgContext root = parser.prog();

			// Display tree
			Trees.inspect(root, parser);
			System.out.println("Done!");
		} catch (Exception e) {
			System.err.println("Could not read file");
		}
	}
}
