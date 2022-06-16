package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTBreakNode extends ASTStmtNode {

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.append(indent);
        outStream.append("BREAK\n");
    }

    @Override
    public void execute() {}

    @Override
    public void codegen(compiler.CompileEnv env) {
        // add jump-instruction to the exit block of the loop to break
        compiler.InstrIntf breakInstr = new compiler.Instr.BreakInstr(env.peekLoopStack());
        env.addInstr(breakInstr);
    }
}
