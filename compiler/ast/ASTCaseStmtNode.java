package compiler.ast;

import compiler.Token;

import java.io.OutputStreamWriter;

public class ASTCaseStmtNode extends ASTStmtNode {
    private final Token caseLiteral;
    private final ASTStmtNode blockStmt;

    public ASTCaseStmtNode(Token caseLiteral, ASTStmtNode blockStmt) {
        this.caseLiteral = caseLiteral; this.blockStmt = blockStmt;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {

    }

    @Override
    public void execute() {

    }
}
