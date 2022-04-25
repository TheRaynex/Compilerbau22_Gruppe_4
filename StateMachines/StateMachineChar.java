package StateMachines;

import compiler.StateMachine;
import compiler.State;

public class StateMachineChar extends StateMachine {

	@Override
	public void initStateTable() {
		State start = new State("start");
		start.addTransition('\'', "afterQuote");
		m_stateMap.put("start", start);

		State afterQuote = new State("afterQuote");
		for ( char c = ' '; c <= '~'; c++) {
			afterQuote.addTransition(c, "beforeQuote");
		}
		m_stateMap.put("afterQuote", afterQuote);
		
		State beforeQuote = new State("beforeQuote");
		beforeQuote.addTransition('\'', "end");
		m_stateMap.put("beforeQuote", beforeQuote);
		
		State end = new State("end");
		m_stateMap.put("end", end);
	}

	@Override
	public String getStartState() {
		return "start";
	}

	@Override
	public boolean isFinalState() {
		return (m_state.equals("end"));	
	}

    public String getName() {
        return "Char";
    }


}
