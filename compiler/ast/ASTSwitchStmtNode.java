package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTSwitchStmtNode extends ASTStmtNode {
    private final ASTExprNode expr;
    private final ASTStmtNode caselist;

    public ASTSwitchStmtNode(ASTExprNode expr, ASTStmtNode caselist) {
        this.expr = expr; this.caselist = caselist;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {

    }

    @Override
    public void execute() {

    }
}
