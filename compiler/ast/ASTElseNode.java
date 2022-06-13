package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTElseNode extends ASTStmtNode {

    private final ASTStmtNode m_content;
    private static int m_index = 0;

    public ASTElseNode(ASTStmtNode m_content) {
        this.m_content = m_content;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.append(indent);
        outStream.append("ELSE\n");

        String childIndent = indent + "  ";
        m_content.print(outStream, childIndent);
    }

    @Override
    public void execute() {
        m_content.execute();
    }

    @Override
    public void codegen(compiler.CompileEnv env) throws Exception {
        // create code blocks needed for control structure
        compiler.InstrBlock body = env.createBlock("else_body_" + m_index);
        compiler.InstrBlock exit = env.createBlock("else_exit_" + m_index);
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
