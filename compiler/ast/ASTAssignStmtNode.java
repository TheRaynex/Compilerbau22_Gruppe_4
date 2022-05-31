package compiler.ast;

import java.io.OutputStreamWriter;

import compiler.Symbol;

public class ASTAssignStmtNode extends ASTStmtNode {

    private final ASTExprNode exprNode;
    private final Symbol symbol;

    public ASTAssignStmtNode(ASTExprNode node, Symbol symbol) {
        this.exprNode = node;
        this.symbol = symbol;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.append(indent);
        outStream.append("ASSIGN\n");
        String childIndent = indent + "  ";
        exprNode.print(outStream, childIndent);
    }

    @Override
    public void execute() {
        symbol.m_number = exprNode.eval();
    }

}
