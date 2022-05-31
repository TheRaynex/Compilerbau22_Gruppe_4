package compiler.ast;

public class ASTNode {
    protected compiler.InstrIntf m_instr; 
    public void codegen(compiler.CompileEnv env) {}
    public compiler.InstrIntf getInstr() {
      return m_instr;   
    }
}
