package compiler.ast;

import compiler.*;

import java.io.OutputStreamWriter;

public class ASTCaseStmtNode extends ASTStmtNode {
    private final Token caseLiteral;
    private final ASTStmtNode blockStmt;

    public ASTCaseStmtNode(Token caseLiteral, ASTStmtNode blockStmt) {
        this.caseLiteral = caseLiteral; this.blockStmt = blockStmt;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        outStream.write("CASE " + caseLiteral.m_type + "\n");
        blockStmt.print(outStream, indent + "   ");
    }

    @Override
    public void execute() {
        blockStmt.execute();
    }

    public void codegen(CompileEnv env, InstrIntf cond, compiler.InstrBlock switch_exit, int no) {
        compiler.InstrBlock exec = env.createBlock("case_exec_" + no);
        compiler.InstrBlock check = env.createBlock("case_check_" + no);
        compiler.InstrBlock exit = env.createBlock("case_exit_" + no);

        compiler.InstrIntf jmpIntoBlock = new compiler.Instr.JumpInstr(check);
        env.addInstr(jmpIntoBlock);

        env.setCurrentBlock(check);

        var literal = new Instr.IntegerLiteralInstr(Integer.parseInt(caseLiteral.m_value));
        var condMeetsLiteral = new Instr.CompareEqualInstr(cond, literal);
        env.addInstr(condMeetsLiteral);

        compiler.InstrIntf jmpCondIntoExec = new Instr.JumpCondInstr(condMeetsLiteral, exec, exit);
        env.addInstr(jmpCondIntoExec);

        env.setCurrentBlock(exec);
        blockStmt.codegen(env);
        compiler.InstrIntf jmpToExit = new compiler.Instr.JumpInstr(switch_exit);
        env.addInstr(jmpToExit);

        env.setCurrentBlock(exit);
    }

    public void execute(int value) {
        var literal = Integer.parseInt(caseLiteral.m_value);

        if (value == literal) this.execute();
    }
}
