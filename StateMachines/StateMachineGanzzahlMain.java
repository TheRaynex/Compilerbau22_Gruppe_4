package StateMachines;

import java.io.OutputStreamWriter;

public class StateMachineGanzzahlMain {
	public static void main(String[] args) throws Exception {

		compiler.StateMachine ganzzahlMachine = new StateMachineGanzzahl();
		OutputStreamWriter outStream = new OutputStreamWriter(System.out, "UTF-8");
		ganzzahlMachine.process("102", outStream);

		System.out.println();
		System.out.println(ganzzahlMachine.asDot());
		System.out.println();
	}
}
