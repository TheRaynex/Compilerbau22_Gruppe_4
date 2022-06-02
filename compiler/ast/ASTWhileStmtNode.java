package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTWhileStmtNode extends ASTStmtNode {
	
	private ASTExprNode exprNode;
	private ASTStmtNode blockstmt;
	
	public ASTWhileStmtNode(ASTExprNode exprNode, ASTStmtNode blockstmt) {
		this.exprNode = exprNode;
		this.blockstmt = blockstmt;
	}

	@Override
	public void print(OutputStreamWriter outStream, String indent) throws Exception {

	}

	@Override
	public void execute() {

	}

}
