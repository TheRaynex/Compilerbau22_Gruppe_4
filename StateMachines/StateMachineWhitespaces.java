package StateMachines;

import compiler.*;

public class StateMachineWhitespaces extends StateMachine {

	@Override
	public String getStartState() {
		return "start";
	}

	@Override
	public boolean isFinalState() {
		return (m_state.equals("end"));
	}
	
	public String getName() {
        return "Whitespaces";
    }

	@Override
	public void initStateTable() {
		State start = new State("start");
		State end = new State("end");
//		State afterSlash = new State("afterSlash");
		start.addTransition('/', "afterSlash");
		start.addTransition(' ', "end");
		start.addTransition('\n', "end");
		start.addTransition('\r', "end");
		start.addTransition('\t', "end");
//		start.addTransition((char) 0x20, "end");  //Space
//		start.addTransition((char) 0x09, "end");  //Tab
//		start.addTransition((char) 0x0B, "end");  //Tab
//		start.addTransition((char) 0x0A, "end");  //new line
//		start.addTransition((char) 0x0C, "end");  //new line
//		start.addTransition((char) 0x0D, "end");  //new line
		m_stateMap.put("start", start);
//		afterSlash.addTransition('n', "end");
//		afterSlash.addTransition('r', "end");
//		m_stateMap.put("afterSlash", afterSlash);
		end.addTransition(' ', "end");
		end.addTransition('\n', "end");
		end.addTransition('\r', "end");
		end.addTransition('\t', "end");
		m_stateMap.put("end", end);
	}
	
}
