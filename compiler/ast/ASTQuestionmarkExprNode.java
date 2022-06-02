package compiler.ast;

import java.io.OutputStreamWriter;

import compiler.CompileEnv;

public class ASTQuestionmarkExprNode extends ASTExprNode {

	private ASTExprNode toEval;
	private ASTExprNode trueCase;
	private ASTExprNode falseCase;

	public ASTQuestionmarkExprNode(ASTExprNode toEval, ASTExprNode trueCase, ASTExprNode falseCase) {
		super();
		this.toEval = toEval;
		this.trueCase = trueCase;
		this.falseCase = falseCase;
	}

	@Override
	public void print(OutputStreamWriter outStream, String indent) throws Exception {
		outStream.write(indent);
		outStream.write("Questionmark\n");
		String childIndent = indent + "  ";
		toEval.print(outStream, childIndent);
		trueCase.print(outStream, childIndent);
		falseCase.print(outStream, childIndent);
	}

	@Override
	public int eval() {
		return toEval.eval() != 0 ? trueCase.eval() : falseCase.eval();
	}
	
	@Override
	public void codegen(CompileEnv env) {
        // trigger codegen for all child nodes
        toEval.codegen(env);
        compiler.InstrIntf toEvalInstr = toEval.getInstr();
        trueCase.codegen(env);
        compiler.InstrIntf trueInstr = trueCase.getInstr();
        falseCase.codegen(env);
        compiler.InstrIntf falseInstr = falseCase.getInstr();

        // create instruction object
        // pass instruction objects of childs  // as input arguments
        // store instruction in this AST node
            m_instr = new compiler.Instr.QuestionMarkInstr(toEvalInstr, trueInstr, falseInstr);
        

        // add instruction to current code block
        env.addInstr(m_instr);
		super.codegen(env);
	}

}
