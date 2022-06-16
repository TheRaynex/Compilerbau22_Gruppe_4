package compiler.ast;

import compiler.Token;

import java.io.OutputStreamWriter;

public class ASTAndOrExprNode extends ASTExprNode {

    public ASTExprNode m_lhs;
    public ASTExprNode m_rhs;
    public compiler.Token.Type m_type;


    public ASTAndOrExprNode(ASTExprNode lhs, ASTExprNode rhs, compiler.TokenIntf.Type type) {
        m_lhs = lhs;
        m_rhs = rhs;
        m_type = type;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        if (m_type == compiler.Token.Type.AND) {
            outStream.write("&& \n");
        } else {
            outStream.write("|| \n");
        }
        String childIndent = indent + "  ";
        m_lhs.print(outStream, childIndent);
        m_rhs.print(outStream, childIndent);
    }

    @Override
    public int eval() {
        if (m_type == Token.Type.AND) {
            if(m_lhs.eval() == 1){
                return m_rhs.eval();
            }else{
                return 0;
            }
        } else {
            if(m_lhs.eval() == 1){
                return 1;
            }else{
                return m_rhs.eval();            
            }
        }
    }

    @Override
    public void codegen(compiler.CompileEnv env) throws Exception {
        // trigger codegen for all child nodes
        m_lhs.codegen(env);
        compiler.InstrIntf lhs = m_lhs.getInstr();
        m_rhs.codegen(env);
        compiler.InstrIntf rhs = m_rhs.getInstr();

        // create instruction object
        // pass instruction objects of childs  // as input arguments
        if (m_type == compiler.Token.Type.AND) {
            // store instruction in this AST node
            m_instr = new compiler.Instr.AndInstr(lhs, rhs);
        } else {
            // store instruction in this AST node
            m_instr = new compiler.Instr.OrInstr(lhs, rhs);
        }

        // add instruction to current code block
        env.addInstr(m_instr);
    }
}
