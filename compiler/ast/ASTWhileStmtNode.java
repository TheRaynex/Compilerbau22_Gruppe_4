package compiler.ast;

import java.io.OutputStreamWriter;

import compiler.CompileEnv;
import compiler.Instr;
import compiler.InstrBlock;
import compiler.InstrIntf;

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
		while (exprNode.eval() != 0) {
			blockstmt.execute();
		}
	}

	@Override
	public void codegen(CompileEnv env) {
		// trigger codegen for all child nodes
		InstrBlock while_head = env.createBlock("while_head");
		InstrBlock while_body = env.createBlock("while_body");
		InstrBlock exit = env.createBlock("while_exit");

		InstrIntf jumpToHead = new Instr.JumpInstr(while_head);
		env.addInstr(jumpToHead);

		env.setCurrentBlock(while_head);
		this.exprNode.codegen(env);
		InstrIntf jumpToBody = new Instr.JumpCondInstr(exprNode.getInstr(), while_body, exit);
		env.addInstr(jumpToBody);

		env.setCurrentBlock(while_body);
		this.blockstmt.codegen(env);
		env.addInstr(jumpToHead);

		env.setCurrentBlock(exit);
	}

}
