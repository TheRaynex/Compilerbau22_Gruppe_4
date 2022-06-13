package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTFuncCallStmtNode extends ASTStmtNode {
    
    private ASTFuncCallExprNode m_call;

    public ASTFuncCallStmtNode(ASTFuncCallExprNode call) {
        m_call = call;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        outStream.write(String.format("CALL %s\n", m_call.m_identifier));
    }

    @Override
    public void execute() {
    }
    
    @Override
    public void codegen(compiler.CompileEnv env) throws Exception {
        m_call.codegen(env);
    }

}
