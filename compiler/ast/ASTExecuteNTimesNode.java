package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTExecuteNTimesNode extends ASTStmtNode {
	ASTExprNode m_n;
	ASTBlockStmtNode m_block = new ASTBlockStmtNode();
	private int m_index;

	@Override
	public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.append(indent);
        outStream.append("EXECUTE N TIMES");

        String childIndent = indent + "  ";
        m_block.print(outStream, childIndent);
	}
	public ASTExecuteNTimesNode(ASTExprNode n, ASTStmtNode block) {
		this.m_n = n;
		this.m_block.addStatement(block);
	}

	@Override
	public void execute() {
		for(int i = 0; i<m_n.eval();i++) {
			m_block.execute();
		}
	}
	
	public void addBlock(ASTBlockNode node) {
		m_block.addStatement(node);
	}
	
    @Override
    public void codegen(compiler.CompileEnv env) {

        
        // create code blocks needed for control structure
        compiler.InstrBlock body = env.createBlock("loop_body_" + m_index);
        compiler.InstrBlock head = env.createBlock("loop_head_" + m_index);
        compiler.InstrBlock exit = env.createBlock("loop_exit_" + m_index);
        m_index++;
        // current block of CompileEnv is our entry block
        // terminate entry block with jump/conditional jump
        // into block of control structure
        compiler.InstrIntf jmpToHead = new compiler.Instr.JumpInstr(head);
        compiler.InstrIntf jmpToBody = new compiler.Instr.JumpCondInstr(jmpToHead, head, exit); //what is the cond ??
        env.addInstr(jmpToHead);
        env.setCurrentBlock(head);
        env.addInstr(jmpToBody);
        
        // for each block of control structure
        // switch CompileEnv to the corresponding block
        env.setCurrentBlock(body);
        env.addInstr(jmpToHead);
        // trigger codegen of statements that
        // belong into this block
        m_n.codegen(env);
        m_block.codegen(env);
        // terminate current block with jump
        compiler.InstrIntf jmpToExit = new compiler.Instr.JumpInstr(exit);
        env.addInstr(jmpToExit);

        // switch CompileEnv to exit block
        env.setCurrentBlock(exit);
    }

}
