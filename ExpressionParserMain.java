import java.io.OutputStreamWriter;

public class ExpressionParserMain {

    public static void main(String[] args) throws Exception {
        compiler.Lexer lexer = new compiler.Lexer();
        compiler.Parser exprParser = new compiler.Parser(lexer);
        System.out.println(expr.eval());
        compiler.ast.ASTExprNode expr = exprParser.parseExpression("6 & 3");
        OutputStreamWriter outStream = new OutputStreamWriter(System.out, "UTF-8");
        expr.print(outStream, "");
        outStream.flush();
    }

}
