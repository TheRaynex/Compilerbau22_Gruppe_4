package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTAssignStmtNode extends ASTStmtNode {

	private final ASTExprNode node;

	public ASTAssignStmtNode(ASTExprNode node) {
		this.node = node;
	}

	@Override
	public void print(OutputStreamWriter outStream, String indent) throws Exception {
		outStream.append(indent);
        outStream.append("ASSIGN\n");
        String childIndent = indent + "  ";
        node.print(outStream, childIndent);
	}

	@Override
	public void execute() {
		System.out.println(node.eval());
	}

}
