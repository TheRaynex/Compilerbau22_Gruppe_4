package compiler.ast;

import compiler.Token;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ASTShiftExprNode extends ASTExprNode {
    private ASTExprNode m_lhs;
    private ASTExprNode m_rhs;
    private compiler.Token.Type m_type;


    public ASTShiftExprNode(ASTExprNode lhs, ASTExprNode rhs, compiler.TokenIntf.Type type) {
        m_lhs = lhs;
        m_rhs = rhs;
        m_type = type;
    }


    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        // TODO Auto-generated method stub
        outStream.write(indent);
        if(m_type == Token.Type.SHIFTLEFT){
            outStream.write("SHIFTLEFT \n");
        }else{
            outStream.write("SHIFTRIGHT \n");
        }
        String childIndent = indent + " ";
        m_lhs.print(outStream, childIndent);
        m_rhs.print(outStream, childIndent);
    }
    
    
    @Override
    public int eval() {
        if(m_type == Token.Type.SHIFTLEFT){
            return m_lhs.eval() << m_rhs.eval();
        } else {
            return m_lhs.eval() >> m_rhs.eval();            
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
        if (m_type == compiler.Token.Type.SHIFTLEFT) {
            // store instruction in this AST node
            m_instr = new compiler.Instr.ShiftLeftInstr(lhs, rhs);
        } else {
            // store instruction in this AST node
            m_instr = new compiler.Instr.ShiftRightInstr(lhs, rhs);            
        }

        // add instruction to current code block
        env.addInstr(m_instr);
    }

    
}
