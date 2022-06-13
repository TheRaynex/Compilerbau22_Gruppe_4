package compiler.ast;

import compiler.InstrBlock;

import java.io.OutputStreamWriter;
import java.util.List;

public class ASTFuncDefStmtNode extends ASTStmtNode {
    
    private String m_identifier;
    private List<String> m_params;
    private ASTBlockStmtNode m_body;
    private static int m_index;

    public ASTFuncDefStmtNode(String identifier, List<String> params, ASTBlockStmtNode body) {
        m_identifier = identifier;
        m_params = params;
        m_body = body;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.append(indent);
        
        // Print function signature
        String paramList = String.join(", ", m_params);
        outStream.append(String.format("FUNCTION %s(%s)\n", m_identifier, paramList));
        
        // Print children
        String childIndent = indent + "  ";
        m_body.print(outStream, childIndent);
    }

    @Override
    public void execute() {
        return;
    }

    @Override
    public void codegen(compiler.CompileEnv env) {
        // Store current block, so that statements following
        // the declaration can be assigned to it. The function
        // declaration is disconnected from the current block,
        // as it is just a block definition that is entered using
        // a call.
        InstrBlock current = env.getCurrentBlock();
        
        // Create function body block and assign to function info
        compiler.InstrBlock body = env.createBlock("function_" + m_index);
        env.getFunctionTable().getFunction(m_identifier).setEntry(body);
        m_index++;

        // Set function body as current block and assign
        // statements inside body to that block
        env.setCurrentBlock(body);
        m_body.codegen(env);
        
        // Set old context again since function declaration is done
        // and code following shall be assigned to old context
        env.setCurrentBlock(current);
    }

}
