import java.io.OutputStreamWriter;

public class ExpressionParserMain {

    public static void main(String[] args) throws Exception {
        compiler.CompileEnv compileEnv = new compiler.CompileEnv("", false);
        compiler.Lexer lexer = new compiler.Lexer();
        compiler.Parser exprParser = new compiler.Parser(compileEnv, lexer);
        compiler.ast.ASTExprNode expr = exprParser.parseExpression("6 & 3*2+4");
        System.out.println(expr.eval());
        OutputStreamWriter outStream = new OutputStreamWriter(System.out, "UTF-8");
        expr.print(outStream, "");
        outStream.flush();
    }

}
