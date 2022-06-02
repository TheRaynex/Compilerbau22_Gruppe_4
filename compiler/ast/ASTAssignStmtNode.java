package compiler.ast;

import compiler.Instr;
import java.io.OutputStreamWriter;

import compiler.Symbol;

public class ASTAssignStmtNode extends ASTStmtNode {

    private final ASTExprNode exprNode;
    private final Symbol symbol;

    public ASTAssignStmtNode(ASTExprNode node, Symbol symbol) {
        this.exprNode = node;
        this.symbol = symbol;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.append(indent);
        outStream.append("ASSIGN\n");
        String childIndent = indent + "  ";
        exprNode.print(outStream, childIndent);
    }

    @Override
    public void execute() {
        symbol.m_number = exprNode.eval();
    }

    @Override
    public void codegen(compiler.CompileEnv env) {
        // trigger codegen for all child nodes
        this.exprNode.codegen(env);
        compiler.InstrIntf instrToEval = this.exprNode.getInstr();

        // create instruction object
        m_instr = new Instr.VarAssignInstr(instrToEval, symbol);

        // add instruction to current code block
        env.addInstr(m_instr);
    }

}
