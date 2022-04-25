package StateMachines;

import java.io.OutputStreamWriter;

public class StateMachineDecimalsMain {

	public static void main(String[] args) throws Exception {
		compiler.StateMachine decimalMachine = new StateMachineDecimals();
		OutputStreamWriter outStream = new OutputStreamWriter(System.out, "UTF-8");
		decimalMachine.process("-417.18", outStream);

        System.out.println();
        System.out.println(decimalMachine.asDot());
		System.out.println();
	}

}
