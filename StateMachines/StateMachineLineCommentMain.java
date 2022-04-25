package StateMachines;

import java.io.OutputStreamWriter;

public class StateMachineLineCommentMain {

	public static void main(String[] args) throws Exception {
		compiler.StateMachine lineCommentMachine = new StateMachineLineComment();
		OutputStreamWriter outStream = new OutputStreamWriter(System.out, "UTF-8");
		lineCommentMachine.process("//test\n", outStream);
	}

}
