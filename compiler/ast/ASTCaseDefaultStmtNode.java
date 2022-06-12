package compiler.ast;

import compiler.CompileEnv;
import compiler.Instr;
import compiler.InstrBlock;
import compiler.InstrIntf;

import java.io.OutputStreamWriter;

public class ASTCaseDefaultStmtNode extends ASTCaseListElementStmtNode {
    public ASTCaseDefaultStmtNode(ASTStmtNode blockStmt) {
        super(blockStmt);
    }

    @Override
    public void codegen
            (CompileEnv env, InstrIntf cond, InstrBlock switch_exit, int no)
            throws Exception {
        compiler.InstrBlock exec = env.createBlock("case_default_exec");
        compiler.InstrBlock exit = env.createBlock("case_default_exit");

        compiler.InstrIntf jmpIntoBlock = new compiler.Instr.JumpInstr(exec);
        env.addInstr(jmpIntoBlock);

        env.setCurrentBlock(exec);
        blockStmt.codegen(env);
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        outStream.write("DEFAULT\n");
        blockStmt.print(outStream, indent + "   ");
    }
}
