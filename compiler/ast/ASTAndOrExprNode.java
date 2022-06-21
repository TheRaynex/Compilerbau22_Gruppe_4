package compiler.ast;

import compiler.Instr;
import compiler.InstrBlock;
import compiler.InstrIntf;
import compiler.Symbol;
import compiler.Token;

import java.io.OutputStreamWriter;

public class ASTAndOrExprNode extends ASTExprNode {

    public ASTExprNode m_lhs;
    public ASTExprNode m_rhs;
    public Token.Type m_type;
    private static int m_index = 0;

    public ASTAndOrExprNode(ASTExprNode lhs, ASTExprNode rhs, compiler.TokenIntf.Type type) {
        m_lhs = lhs;
        m_rhs = rhs;
        m_type = type;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        if (m_type == Token.Type.AND) {
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
        int thisIndex = m_index;
        m_index++;
        InstrBlock left = env.createBlock("left_condition");
        InstrBlock right = env.createBlock("right_condition");
        InstrBlock result1 = env.createBlock("result_true");
        InstrBlock result0 = env.createBlock("result_false");
        InstrBlock exit = env.createBlock("exit");

        Symbol symbol = env.getSymbolTable().createSymbol("$result_"+thisIndex);

        InstrIntf jumptoExit = new Instr.JumpInstr(exit);
        InstrIntf jumpIntoLeft = new Instr.JumpInstr(left);
        env.addInstr(jumpIntoLeft); 

        env.setCurrentBlock(left);
        m_lhs.codegen(env);
        if(m_type == Token.Type.AND){
            var jumpIntoRight = new Instr.JumpCondInstr(m_lhs.getInstr(), right, result0);
            env.addInstr(jumpIntoRight);
        }else{
            var jumpIntoRight = new Instr.JumpCondInstr(m_lhs.getInstr(), result1, right);
            env.addInstr(jumpIntoRight);
        }

        env.setCurrentBlock(right);
        m_rhs.codegen(env);
        var JumpIntoResult = new Instr.JumpCondInstr(m_rhs.getInstr(), result1, result0);
        env.addInstr(JumpIntoResult);

        env.setCurrentBlock(result0);
        InstrIntf res0 = new Instr.IntegerLiteralInstr(0);
        InstrIntf resul0 = new Instr.VarAssignInstr(res0, symbol);
        env.addInstr(resul0);
        env.addInstr(jumptoExit); 
        
        env.setCurrentBlock(result1);
        InstrIntf res1 = new Instr.IntegerLiteralInstr(1);
        InstrIntf resul1 = new Instr.VarAssignInstr(res1, symbol);
        env.addInstr(resul1);
        env.addInstr(jumptoExit); 

        env.setCurrentBlock(exit);
        InstrIntf resultInstr = new Instr.VarAccessInstr("$result_" + thisIndex);
        env.addInstr(resultInstr);
        this.m_instr  = resultInstr;
    }
}
