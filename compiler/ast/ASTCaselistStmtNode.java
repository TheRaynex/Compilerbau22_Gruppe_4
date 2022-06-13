package compiler.ast;

import compiler.CompileEnv;
import compiler.Instr;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ASTCaselistStmtNode extends ASTStmtNode {
    private final List<ASTCaseListElementStmtNode> caseList = new ArrayList<>();
    private final ASTExprNode expr;

    public ASTCaselistStmtNode(ASTExprNode expr) {
        this.expr = expr;
    }

    public void addCase(ASTCaseListElementStmtNode c) {
        caseList.add(c);
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        expr.print(outStream, indent);
        for (ASTStmtNode child : caseList) {
            child.print(outStream, indent);
        }
    }

    @Override
    public void execute() {
        var value = expr.eval();
        for (ASTCaseListElementStmtNode caseNode : caseList) {
            caseNode.execute(value);
        }
    }

    @Override
    public void codegen(CompileEnv env) throws Exception {
        compiler.InstrBlock exit = env.createBlock("switch_exit");
        compiler.InstrBlock body = env.createBlock("switch_body");

        compiler.InstrIntf jmpIntoBlock = new compiler.Instr.JumpInstr(body);
        env.addInstr(jmpIntoBlock);

        env.setCurrentBlock(body);

        expr.codegen(env);
        var exprInstr = expr.getInstr();

        for (int i = 0; i <  caseList.size(); i++) {
            var caseNode = caseList.get(i);
            caseNode.codegen(env, exprInstr, exit, i);
        }

        compiler.InstrIntf jmpToExit = new compiler.Instr.JumpInstr(exit);
        env.addInstr(jmpToExit);
        env.setCurrentBlock(exit);
    }
}
