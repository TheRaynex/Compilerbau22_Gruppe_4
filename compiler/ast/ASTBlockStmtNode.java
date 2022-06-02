package compiler.ast;

import java.io.OutputStreamWriter;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

public class ASTBlockStmtNode extends ASTStmtNode {

    private List<ASTStmtNode> statements;

    public ASTBlockStmtNode() {
        this.statements = new ArrayList<>();

    }

    public void addStatement(ASTStmtNode stmtNode) {
        this.statements.add(stmtNode);
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        this.statements.forEach(node -> {
            try {
                node.print(outStream, indent);
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
        // trigger codegen for all child nodes
        this.statements.forEach(node -> node.codegen(env));
    }
}
