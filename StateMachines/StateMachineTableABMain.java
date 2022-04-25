package StateMachines;

import java.io.OutputStreamWriter;

public class StateMachineTableABMain {

	public static void main(String[] args) throws Exception {
		compiler.StateMachine abMachine = new StateMachineTableAB();
		OutputStreamWriter outStream = new OutputStreamWriter(System.out, "UTF-8");
		abMachine.process("ABB", outStream);

		System.out.println();
		System.out.println(abMachine.asDot());
		System.out.println();
	}

}