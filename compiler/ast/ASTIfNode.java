package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTIfNode extends ASTStmtNode{

    private final ASTExprNode m_conditon;
    private final ASTStmtNode m_content;
    private final ASTStmtNode m_elseblock;
    private static int m_index = 0;

    public ASTIfNode(ASTExprNode m_conditon, ASTStmtNode m_content, ASTStmtNode m_elseblock) {
        this.m_conditon = m_conditon;
        this.m_content = m_content;
        this.m_elseblock = m_elseblock;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {

    }

    @Override
    public void execute() {

    }

//    ifstmt: IF LPAREN condition RPAREN blockstmt elsestmthead
//    elsestmthead: ELSE elsebody | EPSILON
//    elsebody: ifstmt
//    elsebody: blockstmt

    @Override
    public void codegen(compiler.CompileEnv env) {
        // create code blocks needed for control structure
        compiler.InstrBlock body = env.createBlock("block_body_" + m_index);
        compiler.InstrBlock exit = env.createBlock("block_exit_" + m_index);
        m_index++;
        // current block of CompileEnv is our entry block
        // terminate entry block with jump/conditional jump
        // into block of control structure
        compiler.InstrIntf jmpIntoBlock = new compiler.Instr.JumpInstr(body);
        env.addInstr(jmpIntoBlock);

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
