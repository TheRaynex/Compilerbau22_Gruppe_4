package compiler.ast;

import compiler.Instr;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ASTLoopNode extends ASTStmtNode {

    private List<ASTStmtNode> statements;
    private static int m_index = 0;

    public ASTLoopNode(List<ASTStmtNode> statements) {
        this.statements = statements;
    }

    public ASTLoopNode() {
        this.statements = new ArrayList<>();
    }

    public void addStatement(ASTStmtNode stmtNode) {
        this.statements.add(stmtNode);
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {

        outStream.append(indent);
        outStream.append("LOOP\n");
        String childIndent = indent + "  ";

        this.statements.forEach(node -> {
            try {
                node.print(outStream, childIndent);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public void execute() {
        this.statements.forEach(node -> node.execute());
    }

    @Override
    public void codegen(compiler.CompileEnv env) {
        // create code blocks needed for control structure
        compiler.InstrBlock body = env.createBlock("loop_body_" + m_index);
        compiler.InstrBlock exit = env.createBlock("loop_exit_" + m_index);
        m_index++;
        
        // current block of CompileEnv is our entry block
        // terminate entry block with jump/conditional jump
        // into block of control structure

        compiler.InstrIntf jmpIntoBody = new compiler.Instr.JumpInstr(body);
        env.addInstr(jmpIntoBody);
        env.pushLoopStack(exit);
        // for each block of control structure
        // switch CompileEnv to the corresponding block
        // trigger codegen of statements that
        // belong into this block
        env.setCurrentBlock(body);
        for (int i = 0; i <  statements.size(); i++) {
            statements.get(i).codegen(env);
        }
    
        var jmpToExit = new compiler.Instr.JumpInstr(exit);
        env.addInstr(jmpToExit);
        env.popLoopStack();
        env.setCurrentBlock(exit);
    }
}
