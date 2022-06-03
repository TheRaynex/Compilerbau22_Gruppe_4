package compiler.ast;

import compiler.Instr;
import compiler.SymbolTable;

import java.io.OutputStreamWriter;

public class ASTConditionNode extends ASTExprNode {

    public String identifier;
    private SymbolTable symbolTable;

    public ASTConditionNode(String identifier, SymbolTable symbolTable) {
        this.identifier = identifier;
        this.symbolTable = symbolTable;
    }

    @Override
    public void print(OutputStreamWriter outStream, String indent) throws Exception {
        outStream.write(indent);
        outStream.write("CONDITION ");
        outStream.write(this.identifier);
        outStream.write(" != 0\n");
    }

    @Override
    public int eval() {
        if (this.symbolTable.getSymbol(this.identifier).m_number != 0) {
            return 1;
        } else {
            return 0;
        }
    }


    @Override
    public void codegen(compiler.CompileEnv env) {
        // create instruction object
        m_instr = new Instr.ConditionInstr(identifier);

        // add instruction to current code block
        env.addInstr(m_instr);
    }
}
