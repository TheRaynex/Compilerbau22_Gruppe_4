package compiler.ast;

import compiler.Instr;

import java.io.OutputStreamWriter;

public class ASTBreakNode extends ASTStmtNode {

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.append(indent);
        outStream.append("BREAK\n");
    }

    @Override
    public void execute() {
        // TODO
    }

    @Override
    public void codegen(compiler.CompileEnv env) {
        compiler.InstrIntf breakInstr = new compiler.Instr.BreakInstr(env.peekLoopStack());
        env.addInstr(breakInstr);
    }
}
