
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.StringBuilder;

public class Main {

    public static void main(String[] args) throws Exception{


        String inputFile = null;
        String outputFile = null;
        if ( args.length>0 ) {
            inputFile = args[0];
            outputFile = args[1];
        }

        InputStream is = System.in;
        if ( inputFile!=null ) is = new FileInputStream(inputFile);
        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream(is);

        // create a lexer that feeds off of input CharStream
        Tex_grammarLexer lexer = new Tex_grammarLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        Tex_grammarParser parser = new Tex_grammarParser(tokens);

        ParseTree tree = parser.root(); // begin parsing at init rule

        ParseTreeWalker walker = new ParseTreeWalker(); //create standard walker
        LatexTranslateListener extractor = new LatexTranslateListener();
        walker.walk(extractor,tree);


        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile), "utf-8"));
            writer.append(extractor.sb);
        } catch (IOException ex) {
            // Report
        } finally {
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }


        System.out.println(tree.toStringTree(parser)); // print LISP-style tree


    }
}
