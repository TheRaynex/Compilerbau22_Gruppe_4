package StateMachines;

import compiler.StateMachine;

public class StateMachineKeywords extends StateMachine {
	private String m_keyword;
    private String m_endstate;
	
	
	public StateMachineKeywords(String keyword) {
	    m_keyword = keyword;
	    initStateTableDelayed();
	}
	
	@Override
	public String getStartState() {
		return "Z0";
	}

	@Override
	public boolean isFinalState() {
		return (m_state.equals(m_endstate));
	}

	@Override
    public void initStateTable() {
	    
	}

	public void initStateTableDelayed() {
		m_endstate = "Z"+ m_keyword.length();
		char [] keywordArray =  m_keyword.toCharArray();
		String condition = "Z" + 0;
		int i = 1;
		for (char c : keywordArray) {
			String condition1 = "Z" + i;
			compiler.State state = new compiler.State(condition);
			state.addTransition(c, condition1);
			m_stateMap.put(condition, state);
			i++;
			condition = condition1; 
		}
		compiler.State end = new compiler.State(condition);
		m_stateMap.put(condition, end);
	}

	   public String getName() {
	        return "Keywords";
	    }


}
