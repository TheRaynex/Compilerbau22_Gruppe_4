package compiler.ast;

import java.io.OutputStreamWriter;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

public class ASTBlockStmtNode extends ASTStmtNode {

    public List<ASTStmtNode> m_statements;

    public ASTBlockStmtNode() {
        m_statements = new ArrayList<>();
    }

    public void addStatement(ASTStmtNode stmtNode) {
        m_statements.add(stmtNode);
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        m_statements.forEach(node -> {
            try {
                node.print(outStream, indent);
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public void execute() {
        m_statements.forEach(node -> node.execute());
    }

    @Override
    public void codegen(compiler.CompileEnv env) {
        // trigger codegen for all child nodes
        m_statements.forEach(node -> {
            try {
                node.codegen(env);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
