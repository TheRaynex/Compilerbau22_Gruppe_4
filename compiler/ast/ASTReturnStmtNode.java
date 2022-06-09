package compiler.ast;

import compiler.FunctionInfo;
import compiler.Instr;
import compiler.InstrIntf;

import java.io.OutputStreamWriter;

public class ASTReturnStmtNode extends ASTStmtNode {
    
    private FunctionInfo m_context;
    private ASTExprNode m_target;

    public ASTReturnStmtNode(ASTExprNode target) {
        m_target = target;
    }
    
    public void setContext(FunctionInfo context) {
        m_context = context;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        outStream.write("RETURN\n");
    }

    @Override
    public void execute() {
    }
    
    @Override
    public void codegen(compiler.CompileEnv env) throws Exception {
        m_target.codegen(env);
        InstrIntf result = m_target.getInstr();
        
        env.addInstr(new Instr.ReturnInstr(m_context, result));
    }

}
