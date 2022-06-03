package compiler.ast;

import compiler.Instr;
import compiler.InstrIntf;

import java.io.OutputStreamWriter;
import java.util.List;

public class ASTFuncCallExprNode extends ASTExprNode {
    
    public String m_identifier;
    public List<ASTExprNode> m_args;
    
    public ASTFuncCallExprNode(String identifier, List<ASTExprNode> args) {
        m_identifier = identifier;
        m_args = args;
    }
    
    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        outStream.write(String.format("CALL %s\n", m_identifier));
    }
    
    @Override
    public int eval() {
        return 0;
    }
    
    @Override
    public void codegen(compiler.CompileEnv env) throws Exception {
        if (env.getFunctionTable().getFunction(m_identifier) == null) {
            throw new Exception(String.format("Function \"%s\" not defined.", m_identifier));
        }
        
        List<InstrIntf> instructions = this.m_args.stream().map(arg -> {
            // Generate code for each expression
            try {
                arg.codegen(env);
        
                // And get the instruction
                return arg.getInstr();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            
            return null;
        }).toList();
        
        // Generate this instruction and add to environment
        m_instr = new Instr.CallInstr(m_identifier, instructions);
        env.addInstr(m_instr);
    }
    
}
