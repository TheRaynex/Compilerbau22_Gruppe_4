package compiler.ast;

import java.io.OutputStreamWriter;

import compiler.CompileEnv;
import compiler.Instr;
import compiler.InstrBlock;
import compiler.InstrIntf;

public class ASTDoWhileStmtNode extends ASTStmtNode {

	private ASTExprNode exprNode;
	private ASTStmtNode blockstmt;

	public ASTDoWhileStmtNode(ASTExprNode exprNode, ASTStmtNode blockstmt) {
		this.exprNode = exprNode;
		this.blockstmt = blockstmt;
	}

	@Override
	public void print(OutputStreamWriter outStream, String indent) throws Exception {
		outStream.append(indent);
		outStream.append("DO\n");
		String childIndent = indent + "  ";
		blockstmt.print(outStream, childIndent);
		outStream.append("WHILE\n");
		exprNode.print(outStream, childIndent);
	}

	@Override
	public void execute() {
		do {
			blockstmt.execute();
		} while (exprNode.eval() != 0);
	}

	@Override
	public void codegen(CompileEnv env) {
		// trigger codegen for all child nodes
		InstrBlock do_begin = env.createBlock("do_begin");
		InstrBlock exit = env.createBlock("while_exit");

		InstrIntf jumpToHead = new Instr.JumpInstr(do_begin);
		env.addInstr(jumpToHead);

		env.setCurrentBlock(do_begin);
		this.blockstmt.codegen(env);
		compiler.InstrIntf instrToExecute = this.blockstmt.getInstr();
		env.addInstr(instrToExecute);

		this.exprNode.codegen(env);
		compiler.InstrIntf condition = this.exprNode.getInstr();
		env.addInstr(condition);
		InstrIntf jumpToBody = new Instr.JumpCondInstr(condition, do_begin, exit);
		env.addInstr(jumpToBody);

		env.setCurrentBlock(exit);
	}

}
