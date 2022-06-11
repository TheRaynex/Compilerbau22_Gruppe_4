package compiler.ast;

import compiler.CompileEnv;

import java.io.OutputStreamWriter;

public class ASTSwitchStmtNode extends ASTStmtNode {
    private final ASTStmtNode caselist;

    public ASTSwitchStmtNode(ASTStmtNode caselist) {
        this.caselist = caselist;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        outStream.write("SWITCH\n");
        var childIndent = indent + "  ";
        caselist.print(outStream, childIndent);
    }

    @Override
    public void execute() {
        caselist.execute();
    }

    @Override
    public void codegen(CompileEnv env) throws Exception {
        caselist.codegen(env);
    }
}
