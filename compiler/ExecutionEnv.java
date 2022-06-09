package compiler;

import compiler.ast.Tuple;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

public class ExecutionEnv implements ExecutionEnvIntf {
    private SymbolTable m_symbolTable;
    private Stack<Tuple<String, Integer>> m_numberStack;
    private Stack<Tuple<FunctionInfo, ListIterator<InstrIntf>>> m_executionStack;
    private ListIterator<InstrIntf> m_instrIter;
    private FunctionInfo m_currentFunction;
    private List<Tuple<String, Integer>> m_currentArgs;
    private OutputStreamWriter m_outStream;
    private FunctionTable m_functionTable;
    private boolean m_trace;

    public ExecutionEnv(FunctionTable functionTable, SymbolTable symbolTable, OutputStream outStream, boolean trace) throws Exception {
		m_symbolTable = symbolTable;
		m_numberStack = new Stack<>();
		m_executionStack = new Stack<>();
		m_outStream = new OutputStreamWriter(outStream, "UTF-8");
		m_functionTable = functionTable;
        m_currentArgs = new ArrayList<>();
		m_trace = trace;
	}
	
    @Override
    public FunctionTable getFunctionTable() {
        return m_functionTable;
    }
    
	public void push(Tuple<String, Integer> value) {
		m_numberStack.push(value);
	}
	
	public Tuple<String, Integer> pop() {
        return m_numberStack.pop();
	}
	
    public List<Tuple<String, Integer>> getCurrentArgs() {
        return m_currentArgs;
    }
    
	public Symbol getSymbol(String symbolName) {
		return m_symbolTable.getSymbol(symbolName);
	}

	public void setInstrIter(ListIterator<InstrIntf> instrIter) { // instrIter == program counter
		m_instrIter = instrIter;
	}
	
    public ListIterator<InstrIntf> getInstrIter() {
        return m_instrIter;
    }
    
    public void execute(ListIterator<InstrIntf> instrIter) throws Exception {
        m_instrIter = instrIter;
        while (m_instrIter.hasNext()) {
            InstrIntf nextInstr = m_instrIter.next();
            if (m_trace) {
                nextInstr.trace(getOutputStream());
                m_outStream.flush();
            }
            nextInstr.execute(this);
        }
    }
	
	public OutputStreamWriter getOutputStream() {
		return m_outStream;
	}

    @Override
    public void pushFunction(FunctionInfo f, List<InstrIntf> arguments) {
        // Save current function
        this.m_executionStack.push(new Tuple<>(m_currentFunction, m_instrIter));

        // Store current arguments
        for (int i = m_currentArgs.size() - 1; i >= 0; i--) {
            m_numberStack.push(m_currentArgs.get(i));
        }
        
        // Activate new function
        m_currentFunction = f;
        setInstrIter(f.m_body.getIterator());
        
        // Load new arguments
        m_currentArgs.clear();
        for (int i = arguments.size() - 1; i >= 0; i--) {
            String identifier = f.varNames.get(i);
            int value = arguments.get(i).m_value;
            m_currentArgs.add(new Tuple<>(identifier, value));
        }
    }

    @Override
    public void popFunction() {
        // Activate previous function
        Tuple<FunctionInfo, ListIterator<InstrIntf>> result = this.m_executionStack.pop();
        m_currentFunction = result._1;
    	setInstrIter(result._2);
        
        // Only read arguments, when there is a function
        if (m_currentFunction == null) {
            return;
        }
        
        // Pop values from stack
        m_currentArgs.clear();
        for (int i = 0; i < m_currentFunction.varNames.size(); i++) {
            m_currentArgs.add(m_numberStack.pop());
        }
    }
}
