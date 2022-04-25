package StateMachines;

import compiler.State;

/**
 * sample state machine
 * accept AB*
 */

public class StateMachineDecimals extends compiler.StateMachine {
	
	@Override
	public String getStartState() {
		return "start";
	}

	@Override
	public boolean isFinalState() {
		return (m_state.equals("end"));
	}

	@Override
	public void initStateTable() {
        for (States v : States.values()) {
            m_stateMap.put(v.name, v.s);
        }

        States.START.s.addTransition('-', States.MINUS.name);
        addTransitionFigure(States.START.s, States.INTEGER.name);

        addTransitionFigure(States.MINUS.s, States.INTEGER.name);

        addTransitionFigure(States.INTEGER.s, States.INTEGER.name);
        States.INTEGER.s.addTransition('.', States.AFTER_POINT.name);

        addTransitionFigure(States.AFTER_POINT.s, States.END.name);

        addTransitionFigure(States.END.s, States.END.name);
	}

	private enum States {
	    START("start"),
        INTEGER("integer"),
        MINUS("minus"),
        AFTER_POINT("afterPoint"),
        END("end");

	    final State s;
	    final String name;

	    States(String name) {
	        this.name = name;
	        this.s = new State(name);
        }
    }

	private void addTransitionFigure(State s, String target) {
	    for(char c = '0'; c <= '9'; c++) {
	        s.addTransition(c, target);
        }
    }

    public String getName() {
	        return "Decimals";
	    }

}
