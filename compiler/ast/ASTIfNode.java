package compiler.ast;

import compiler.Instr;

import java.io.OutputStreamWriter;

public class ASTIfNode extends ASTStmtNode {

    private final ASTExprNode m_condition;
    private final ASTStmtNode m_content;
    private final ASTStmtNode m_elseblock;
    private static int m_index = 0;

    public ASTIfNode(ASTExprNode m_condition, ASTStmtNode m_content, ASTStmtNode m_elseblock) {
        this.m_condition = m_condition;
        this.m_content = m_content;
        this.m_elseblock = m_elseblock;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.append(indent);
        outStream.append("IF\n");

        String childIndent = indent + "  ";
        m_condition.print(outStream, childIndent);
        m_content.print(outStream, childIndent);
        if (m_elseblock != null)
            if (m_elseblock instanceof ASTElseNode) {
                m_elseblock.print(outStream, childIndent);
            } else {
                outStream.append("ELSE ");
                m_elseblock.print(outStream, childIndent);
            }
    }

    @Override
    public void execute() {
        if (m_condition.eval() == 0 && m_elseblock != null) {
            m_elseblock.execute();
        } else {
            m_content.execute();
        }

    }

    @Override
    public void codegen(compiler.CompileEnv env) {
        // create code blocks needed for control structure
        compiler.InstrBlock condition = env.createBlock("if_condition" + m_index);
        compiler.InstrBlock body = env.createBlock("if_body_" + m_index);
        compiler.InstrBlock exit = env.createBlock("if_exit_" + m_index);
        m_index++;
        // current block of CompileEnv is our entry block
        // terminate entry block with jump/conditional jump
        // into block of control structure
        compiler.InstrIntf jmpIntoBlock = new compiler.Instr.JumpInstr(condition);
        env.addInstr(jmpIntoBlock);

        // for each block of control structure
        // switch CompileEnv to the corresponding block
        env.setCurrentBlock(condition);
        // trigger codegen of statements that
        // belong into this block
        m_condition.codegen(env);
        compiler.InstrIntf m_conditionInst = m_condition.getInstr();
        // terminate current block with jump
        compiler.InstrIntf condInstr = new Instr.CompareEqualInstr(m_conditionInst, new Instr.IntegerLiteralInstr(0));
        compiler.InstrIntf jmpToBodyIfValueNotZero = new Instr.JumpCondInstr(condInstr, exit, body);
        env.addInstr(jmpToBodyIfValueNotZero);

        // switch CompileEnv to exit block
        env.setCurrentBlock(body);
        m_content.codegen(env);
        compiler.InstrIntf jmpToExit = new compiler.Instr.JumpInstr(exit);
        env.addInstr(jmpToExit);

        env.setCurrentBlock(exit);
        if (m_elseblock != null) {
            m_elseblock.codegen(env);
        }
    }
}
