package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTExecuteNTimesNode extends ASTStmtNode {
	ASTExprNode m_n;
	ASTBlockStmtNode m_block;
	private int m_index;

	@Override
	public void print(OutputStreamWriter outStream, String indent) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute() {
		for(int i = 0; i<m_n.eval();i++) {
			m_block.execute();
		}
	}
	
	public void addBlock(ASTBlockStmtNode node) {
		m_block.addStatement(node);
	}
	
    @Override
    public void codegen(compiler.CompileEnv env) {
        // trigger codegen for all child nodes
        m_n.codegen(env);
        m_block.codegen(env);
        
        // create code blocks needed for control structure
        compiler.InstrBlock body = env.createBlock("loop_body_" + m_index);
        compiler.InstrBlock head = env.createBlock("loop_head_" + m_index);
        compiler.InstrBlock exit = env.createBlock("loop_exit_" + m_index);
        m_index++;
        // current block of CompileEnv is our entry block
        // terminate entry block with jump/conditional jump
        // into block of control structure
        compiler.InstrIntf jmpToHead = new compiler.Instr.JumpInstr(head);
        compiler.InstrIntf jmpToBody = new compiler.Instr.JumpCondInstr(jmpToHead, head, exit);
        env.addInstr(jmpToHead);
        env.setCurrentBlock(head);
        env.addInstr(jmpToBody);
        
        // for each block of control structure
        // switch CompileEnv to the corresponding block
        env.setCurrentBlock(body);
        // trigger codegen of statements that
        // belong into this block
        m_content.codegen(env);
        // terminate current block with jump
        compiler.InstrIntf jmpToExit = new compiler.Instr.JumpInstr(exit);
        env.addInstr(jmpToExit);

        // switch CompileEnv to exit block
        env.setCurrentBlock(exit);
    }

}
