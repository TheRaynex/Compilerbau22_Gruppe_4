package compiler.ast;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ASTCaselistStmtNode extends ASTStmtNode {
    private final List<ASTStmtNode> caseList = new ArrayList<>();

    public void addCase(ASTStmtNode c) {
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

    }
}
