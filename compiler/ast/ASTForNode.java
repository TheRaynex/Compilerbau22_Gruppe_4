package compiler.ast;

import java.io.OutputStreamWriter;


public class ASTForNode extends ASTStmtNode {

    private ASTStmtNode m_pre_stmt;
    private ASTExprNode m_cond;
    // statement which is executed after every iteration
    private ASTStmtNode m_loop_stmt;
    private ASTStmtNode m_body;
    private static int m_index = 0;

    public ASTForNode(ASTStmtNode preStmt, ASTExprNode cond, ASTStmtNode block, ASTStmtNode loopStmt) {
        m_pre_stmt = preStmt;
        m_cond = cond;
        m_loop_stmt = loopStmt;
        m_body = block;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        outStream.write("FOR\n");
        String childIndent = indent + "  ";
        m_pre_stmt.print(outStream, childIndent);
        outStream.write("\n");
        m_cond.print(outStream, childIndent);
        outStream.write("\n");
        m_loop_stmt.print(outStream, childIndent);
        outStream.write("\n");
        m_body.print(outStream, childIndent);
        outStream.write("\n");
    }

    @Override
    public void execute() {
        m_pre_stmt.execute();
        while (m_cond.eval() != 0) {
            m_body.execute();
            m_loop_stmt.execute();
        }
    }

    @Override
    public void codegen(compiler.CompileEnv env) throws Exception {

        compiler.InstrBlock head = env.createBlock("For_head_" + m_index);
        compiler.InstrBlock body = env.createBlock("For_body_" + m_index);
        compiler.InstrBlock exit = env.createBlock("For_exit_" + m_index);
        m_index++;

        // current block of CompileEnv is our entry block
        // terminate entry block with jump/conditional jump
        m_pre_stmt.codegen(env);
        compiler.InstrIntf jmpIntoCondition = new compiler.Instr.JumpInstr(head);   
        env.addInstr(jmpIntoCondition);

        // head of for loop
        // codegen for condition
        // check condition and do conditional Jump 
        env.setCurrentBlock(head);
        m_cond.codegen(env);
        compiler.Instr.JumpCondInstr condInstr = new compiler.Instr.JumpCondInstr(m_cond.getInstr(), body, exit);
        head.addInstr(condInstr);

        // body of for loop
        // generate code for body
        // generate code for loop stmt
        // jmp back to head
        env.setCurrentBlock(body);
        m_body.codegen(env);
        m_loop_stmt.codegen(env);
        compiler.Instr.JumpInstr jmpToHead = new compiler.Instr.JumpInstr(head);
        env.addInstr(jmpToHead);

        env.setCurrentBlock(exit);
    }
}
