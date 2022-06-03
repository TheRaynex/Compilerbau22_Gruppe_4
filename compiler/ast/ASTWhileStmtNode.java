package compiler.ast;

import java.io.OutputStreamWriter;

import compiler.CompileEnv;

public class ASTWhileStmtNode extends ASTStmtNode {
	
	private ASTExprNode exprNode;
	private ASTStmtNode blockstmt;
	
	public ASTWhileStmtNode(ASTExprNode exprNode, ASTStmtNode blockstmt) {
		this.exprNode = exprNode;
		this.blockstmt = blockstmt;
	}

	@Override
	public void print(OutputStreamWriter outStream, String indent) throws Exception {
		outStream.append(indent);
		outStream.append("WHILE\n");
		String childIndent = indent + "  ";
		exprNode.print(outStream, childIndent);
        blockstmt.print(outStream, childIndent);
	}

	@Override
	public void execute() {
		while(exprNode.eval() > 0) {
			blockstmt.execute();
		}
	}
	
	@Override
	public void codegen(CompileEnv env) {
		 // trigger codegen for all child nodes
        this.exprNode.codegen(env);
        compiler.InstrIntf instrToEval = this.exprNode.getInstr();
        this.blockstmt.codegen(env);
        compiler.InstrIntf instrToExecute = this.blockstmt.getInstr();
        
        // create instruction object
        // add instruction to current code block (?)
	}

}
