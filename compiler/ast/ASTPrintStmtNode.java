package compiler.ast;

import java.io.OutputStreamWriter;

public class ASTPrintStmtNode extends ASTStmtNode {
    private final ASTExprNode node;

    public ASTPrintStmtNode(ASTExprNode node) {
        this.node = node;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.append(indent);
        outStream.append("PRINT\n");

        String childIndent = indent + "  ";
        node.print(outStream, childIndent);
    }

    @Override
    public void execute() {
        System.out.println(node.eval());
    }

    @Override
    public void codegen(compiler.CompileEnv env) throws Exception {
        // trigger codegen for all child nodes
        this.node.codegen(env);
        compiler.InstrIntf instrToPrint = this.node.getInstr();

        // create instruction object
        // pass instruction objects of childs
        // as input arguments
        m_instr = new compiler.Instr.PrintInstr(instrToPrint);

        // add instruction to current code block
        env.addInstr(m_instr);
    }
}
