package compiler.ast;

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
        // Code blocks needed for control structures
        compiler.InstrBlock body = env.createBlock("function_body_" + m_index);
        compiler.InstrBlock exit = env.createBlock("function_exit_" + m_index);
        m_index++;
        
        // Assign body entry to function info
        env.getFunctionTable().getFunction(m_identifier).setEntry(body);

        // for each block of control structure
        // switch CompileEnv to the corresponding block
        // without jumping into it, because it is just
        // a definition
        env.setCurrentBlock(body);
        // trigger codegen of statements that
        // belong into this block
        m_body.codegen(env);
        // terminate current block with jump
        compiler.InstrIntf jmpToExit = new compiler.Instr.JumpInstr(exit);
        env.addInstr(jmpToExit);

        // switch CompileEnv to exit block
        env.setCurrentBlock(exit);
    }

}
