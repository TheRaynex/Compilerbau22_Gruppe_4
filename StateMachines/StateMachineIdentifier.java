package StateMachines;

/**
 * sample state machine
 * accept AB*
 */

public class StateMachineIdentifier extends compiler.StateMachine {

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
        compiler.State start = new compiler.State("start");
        start.addTransition('_', "end");
        addTransitionForLetter(start, "end");
        m_stateMap.put("start", start);

        compiler.State end = new compiler.State("end");
        end.addTransition('_', "end");
        addTransitionForLetter(end, "end");
        addTransitionForNumber(end, "end");
        m_stateMap.put("end", end);
    }

    public String getName() {
        return "Identifier";
    }

}
