package compiler.ast;

import compiler.Token;
import compiler.TokenIntf;

import java.io.OutputStreamWriter;

public class ASTCaseStmtNode extends ASTStmtNode {
    private final Token caseLiteral;
    private final ASTStmtNode blockStmt;

    public ASTCaseStmtNode(Token caseLiteral, ASTStmtNode blockStmt) {
        this.caseLiteral = caseLiteral; this.blockStmt = blockStmt;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        outStream.write("CASE " + caseLiteral.m_type + "\n");
        blockStmt.print(outStream, indent + "   ");
    }

    @Override
    public void execute() {
        blockStmt.execute();
    }

    public void execute(int value) {
        var literal = Integer.parseInt(caseLiteral.m_value);

        if (value == literal) this.execute();
    }
}
