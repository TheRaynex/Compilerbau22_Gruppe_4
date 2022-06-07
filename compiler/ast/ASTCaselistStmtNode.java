package compiler.ast;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ASTCaselistStmtNode extends ASTStmtNode {
    private final List<ASTCaseStmtNode> caseList = new ArrayList<>();
    private final ASTExprNode expr;

    public ASTCaselistStmtNode(ASTExprNode expr) {
        this.expr = expr;
    }

    public void addCase(ASTCaseStmtNode c) {
        caseList.add(c);
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        for (ASTStmtNode child : caseList) {
            child.print(outStream, indent);
        }
    }

    @Override
    public void execute() {
        var value = expr.eval();
        for (ASTCaseStmtNode caseNode : caseList) {
            caseNode.execute(value);
        }
    }
}
