package StateMachines;

import java.io.OutputStreamWriter;

public class StateMachineTableStringLiteralMain {

	public static void main(String[] args) throws Exception {
		compiler.StateMachine stringMachine = new StateMachineTableStringLiteral();
		OutputStreamWriter outStream = new OutputStreamWriter(System.out, "UTF-8");
		stringMachine.process("\"ABB\"", outStream);

        System.out.println();
        System.out.println(stringMachine.asDot());
		System.out.println();
	}

}
