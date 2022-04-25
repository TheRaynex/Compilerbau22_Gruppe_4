package StateMachines;

/**
 * sample state machine
 * accept AB*
 */

public class StateMachineTableAB extends compiler.StateMachine {
	
	@Override
	public String getStartState() {
		return "expectA";
	}

	@Override
	public boolean isFinalState() {
		return (m_state.equals("expectB"));
	}

	@Override
	public void initStateTable() {
		compiler.State expectA = new compiler.State("expectA");
		expectA.addTransition('A', "expectB");
        m_stateMap.put("expectA", expectA);
        compiler.State expectB = new compiler.State("expectB");
        expectB.addTransition('B', "expectB");
		m_stateMap.put("expectB", expectB);
	}

	   public String getName() {
	        return "AB";
	    }

}
