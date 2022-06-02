package compiler.ast;

import compiler.Instr;
import compiler.Symbol;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Collectors;

public class ASTFuncDefStmtNode extends ASTStmtNode {

    private String name;
    private List<Symbol> params;
    private ASTBlockStmtNode blockStmtNode;

    public ASTFuncDefStmtNode(String name, List<Symbol> params,
                              ASTBlockStmtNode blockStmtNode) {
        this.name = name;
        this.params = params;
        this.blockStmtNode = blockStmtNode;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.append(indent);
        String paramsStr = params.stream().map(symbol -> symbol.m_name).collect(
                Collectors.joining(", "));
        outStream.append("FUNCTION (" + paramsStr + ")\n");
        String childIndent = indent + "  ";
        blockStmtNode.print(outStream, childIndent);
    }

    @Override
    public void execute() {
        return;
    }

    @Override
    public void codegen(compiler.CompileEnv env) {
        return;
    }

}
