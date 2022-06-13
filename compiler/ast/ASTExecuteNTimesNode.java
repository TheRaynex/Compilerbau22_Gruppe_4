package compiler.ast;

import java.io.OutputStreamWriter;

import compiler.Instr;
import compiler.InstrBlock;
import compiler.InstrIntf;
import compiler.Symbol;

public class ASTExecuteNTimesNode extends ASTStmtNode {
	ASTExprNode m_n;
	ASTBlockStmtNode m_block = new ASTBlockStmtNode();
	private static int m_index = 0;

	@Override
	public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.append(indent);
        outStream.append("EXECUTE N TIMES\n");

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
    public void codegen(compiler.CompileEnv env) throws Exception {

        int thisIndex = m_index;
        m_index++;

        // create code blocks needed for control structure
        InstrBlock body = env.createBlock("loop_body_" + thisIndex);
        InstrBlock init = env.createBlock("loop_init_" + thisIndex);
        InstrBlock head = env.createBlock("loop_head_" + thisIndex);
        InstrBlock exit = env.createBlock("loop_exit_" + thisIndex);
        // current block of CompileEnv is our entry block
        // terminate entry block with jump/conditional jump
        // into block of control structure

        Symbol symbol = env.getSymbolTable().createSymbol("i_" + thisIndex);

        m_n.codegen(env);
        InstrIntf n = m_n.getInstr();


        InstrIntf acc = new Instr.VarAccessInstr("i_" + thisIndex);
        InstrIntf one = new Instr.IntegerLiteralInstr(1);
        InstrIntf inc = new Instr.AddInstr(acc, one);
        InstrIntf ass = new Instr.VarAssignInstr(inc, symbol);

        InstrIntf resetI = new Instr.VarAssignInstr(new Instr.IntegerLiteralInstr(0), symbol);

        InstrIntf cond = new Instr.CompareLessInstr(acc, n);

        InstrIntf jmpToInit = new Instr.JumpInstr(init);
        InstrIntf jmpToHead = new Instr.JumpInstr(head);
        InstrIntf jmpToBody = new Instr.JumpCondInstr(cond, body, exit);

        env.addInstr(jmpToInit);

        env.setCurrentBlock(init);
        env.addInstr(resetI);
        env.addInstr(jmpToHead);

        env.setCurrentBlock(head);
        env.addInstr(acc);
        env.addInstr(cond);
        env.addInstr(jmpToBody);
        // for each block of control structure
        // switch CompileEnv to the corresponding block
        env.setCurrentBlock(body);

        // trigger codegen of statements that
        // belong into this block
        m_block.codegen(env);
        env.addInstr(acc);
        env.addInstr(one);
        env.addInstr(inc);
        env.addInstr(ass);
        env.addInstr(jmpToHead);

        // terminate current block with jump
        //compiler.InstrIntf jmpToExit = new compiler.Instr.JumpInstr(exit);
        //env.addInstr(jmpToExit);

        // switch CompileEnv to exit block
        env.setCurrentBlock(exit);

    }

}
