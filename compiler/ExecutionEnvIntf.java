package compiler;

import compiler.ast.Tuple;

import java.io.OutputStreamWriter;
import java.util.List;
import java.util.ListIterator;

public interface ExecutionEnvIntf {
    public FunctionTable getFunctionTable();
    
	/**
	 *  push temporary on value stack
	 */
    public void push(Tuple<String, Integer> argument);
    
    /**
     *  pop (consume) temporary from value stack	
     */
	public Tuple<String, Integer> pop();
	/**
	 *  push function on execution stack
	 */
	public void pushFunction(FunctionInfo function, List<InstrIntf> args);
	/**
	 * pop function from execution stack
	 */
	public void popFunction();
    
    public List<Tuple<String, Integer>> getCurrentArgs();
	/**
	 *  get symbol from symbol table
	 */
	public Symbol getSymbol(String symbolName);
    /**
     *  set instruction iterator to the given block
     */
	public void setInstrIter(ListIterator<InstrIntf> instrIter);
    
    public ListIterator<InstrIntf> getInstrIter();
    /**
     *  execute instruction list	
     */
	public void execute(ListIterator<InstrIntf> instrIter) throws Exception;
	/**
	 *  get output stream
	 */
	public OutputStreamWriter getOutputStream();
}
