package compiler.ast;

import compiler.TokenIntf;

import java.io.OutputStreamWriter;

public class ASTParentheseExprNode extends ASTExprNode {

    private final ASTExprNode inner;

    public ASTParentheseExprNode(ASTExprNode inner) {
        this.inner = inner;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        outStream.write("PAREN\n");
        indent += "  ";
        inner.print(outStream, indent);
    }

    @Override
    public int eval() {
        return inner.eval();
    }

    public void codegen(compiler.CompileEnv env) {
        // trigger codegen for all child nodes
        inner.codegen(env);
    }
    
}
