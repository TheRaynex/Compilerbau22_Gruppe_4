package StateMachines;

import java.io.OutputStreamWriter;

public class StateMachineIdentifierMain {

    public static void main(String[] args) throws Exception {
        compiler.StateMachine abMachine = new StateMachineIdentifier();
        OutputStreamWriter outStream = new OutputStreamWriter(System.out, "UTF-8");
        abMachine.process("_BAB", outStream);

        System.out.println();
        System.out.println(abMachine.asDot());
        System.out.println();
    }

}
