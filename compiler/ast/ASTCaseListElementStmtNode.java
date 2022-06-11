package compiler.ast;

import compiler.CompileEnv;
import compiler.InstrIntf;

public abstract class ASTCaseListElementStmtNode extends ASTStmtNode {
    protected final ASTStmtNode blockStmt;

    protected ASTCaseListElementStmtNode(ASTStmtNode blockStmt) {
        this.blockStmt = blockStmt;
    }

    public abstract void codegen(CompileEnv env, InstrIntf cond, compiler.InstrBlock switch_exit, int no);

    @Override
    public void execute() {
        blockStmt.execute();
    }

    public void execute(int value) {
        this.execute();
    }
}
