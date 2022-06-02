package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTCompareExprNode extends ASTExprNode {

    public ASTExprNode m_lhs;
    public ASTExprNode m_rhs;
    public compiler.Token.Type m_type;

    public ASTCompareExprNode(ASTExprNode lhs, ASTExprNode rhs, compiler.TokenIntf.Type type) {
        m_lhs = lhs;
        m_rhs = rhs;
        m_type = type;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        switch (m_type) {
            case LESS:
                outStream.write("< \n");
                break;
            case GREATER:
                outStream.write("> \n");
                break;
            case EQUAL:
                outStream.write("== \n");
                break;
            default:
                break;
        }
        String childIndent = indent + "  ";
        m_lhs.print(outStream, childIndent);
        m_rhs.print(outStream, childIndent);
    }

    @Override
    public int eval() {
        switch (m_type) {
            case LESS:
                return m_lhs.eval() < m_rhs.eval() ? 1 : 0;
            case GREATER:
                return m_lhs.eval() > m_rhs.eval() ? 1 : 0;
            case EQUAL:
                return m_lhs.eval() == m_rhs.eval() ? 1 : 0;
            default:
                return 0;
        }
    }
    
    @Override
    public void codegen(compiler.CompileEnv env) {
        // trigger codegen for all child nodes
        m_lhs.codegen(env);
        compiler.InstrIntf lhs = m_lhs.getInstr();
        m_rhs.codegen(env);
        compiler.InstrIntf rhs = m_rhs.getInstr();

        // create instruction object
        // pass instruction objects of childs  // as input arguments
        switch(m_type){
            case LESS:
                // store instruction in this AST node
                m_instr = new compiler.Instr.CompareLessInstr(lhs, rhs);
                break;
            case GREATER:
                // store instruction in this AST node
                m_instr = new compiler.Instr.CompareGreaterInstr(lhs, rhs);
                break;
            case EQUAL:
                // store instruction in this AST node
                m_instr = new compiler.Instr.CompareEqualInstr(lhs, rhs);
                break;
        }

        // add instruction to current code block
        env.addInstr(m_instr);
    }
}
