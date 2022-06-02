package compiler.ast;

import compiler.Instr;
import compiler.TokenIntf;

import java.io.OutputStreamWriter;

public class ASTUnaryExprNode extends ASTExprNode {

    private final ASTExprNode parenthesisExpr;
    private final compiler.TokenIntf.Type type;

    public ASTUnaryExprNode(ASTExprNode parenthesisExpr, compiler.TokenIntf.Type type) {
        this.parenthesisExpr = parenthesisExpr;
        this.type = type;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);

        if (type != TokenIntf.Type.MINUS && type != TokenIntf.Type.NOT) {
            parenthesisExpr.print(outStream, "");
            return;
        }

        String out = (type == TokenIntf.Type.MINUS) ? "MINUS" : "NOT";
        out += " \n";
        outStream.write(out);

        String childIndent = indent + "  ";
        parenthesisExpr.print(outStream, childIndent);
    }

    @Override
    public int eval() {
        return switch (type) {
            case MINUS -> -parenthesisExpr.eval();
            case NOT -> (parenthesisExpr.eval() == 0) ? 1 : 0;
            default -> parenthesisExpr.eval();
        };
    }

    @Override
    public void codegen(compiler.CompileEnv env) {
        // trigger codegen for all child nodes
        parenthesisExpr.codegen(env);
        compiler.InstrIntf operand = parenthesisExpr.getInstr();

        // create instruction object
        // pass instruction objects of childs  // as input arguments
        switch (type) {
            case NOT:
                m_instr = new Instr.NotInstr(operand); break;
            case MINUS:
                m_instr = new Instr.MinusInstr(operand); break;
            default: m_instr = operand; break;
        }

        // add instruction to current code block
        env.addInstr(m_instr);
    }
}
