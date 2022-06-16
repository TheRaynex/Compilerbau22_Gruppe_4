package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTIntegerLiteralNode extends ASTExprNode {
    public String m_value;
    
    public ASTIntegerLiteralNode(String value) {
        m_value = value;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        outStream.write(String.format("INTEGER %s\n", m_value));
    }

    @Override
    public int eval() {
        return Integer.valueOf(m_value);
    }

    @Override
    public void codegen(compiler.CompileEnv env) {
        // create instruction object
        // pass instruction objects of childs
        // as input arguments
        m_instr = new compiler.Instr.IntegerLiteralInstr(Integer.valueOf(m_value));

        // add instruction to current code block
        env.addInstr(m_instr);
    }

}
