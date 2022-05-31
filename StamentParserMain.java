import java.io.OutputStreamWriter;

public class StamentParserMain {

    public static void main(String[] args) throws Exception {
        compiler.CompileEnv compileEnv = new compiler.CompileEnv("", false);
        compiler.Lexer lexer = new compiler.Lexer();
        compiler.Parser parser = new compiler.Parser(compileEnv, lexer);
        compiler.ast.ASTStmtNode stmt = parser.parseStmt("{ DECLARE a; a = 0; DECLARE b; b = !(!(!a)) ? 5 : 7; PRINT b;}");
        stmt.execute();
        OutputStreamWriter outStream = new OutputStreamWriter(System.out, "UTF-8");
        stmt.print(outStream, "");
        outStream.flush();
    }

}
