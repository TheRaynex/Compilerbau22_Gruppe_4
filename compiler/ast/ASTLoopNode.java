package compiler.ast;

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
    public void codegen(compiler.CompileEnv env) throws Exception {

        // create code blocks needed for control structure
        
        compiler.InstrBlock body = env.createBlock("loop_body_" + m_index);
        compiler.InstrBlock exit = env.createBlock("loop_exit_" + m_index);
        m_index++;
        
        // push the exit block of the loop on the loop-stack for further use
        // loop_body is our entry block => jump

        env.pushLoopStack(exit);

        compiler.InstrIntf jmpIntoBody = new compiler.Instr.JumpInstr(body);
        env.addInstr(jmpIntoBody);
        env.setCurrentBlock(body);

        // trigger codegen for statement list

        statements.forEach(node -> {
            try {
                node.codegen(env);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        // jump back to the body block (=> loop)

        var loop = new compiler.Instr.JumpInstr(body);
        env.addInstr(loop);
        
        // pop the exit-block of the loop stack (codegen done)
        // jump to the exit block

        env.popLoopStack();
        env.setCurrentBlock(exit);
    }
}
