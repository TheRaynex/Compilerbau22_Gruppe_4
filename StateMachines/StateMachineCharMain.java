package StateMachines;

import java.io.OutputStreamWriter;

public class StateMachineCharMain {

    public static void main(String[] args) throws Exception {
        compiler.StateMachine stateMachineChar = new StateMachineChar();
        OutputStreamWriter outStream = new OutputStreamWriter(System.out, "UTF-8");
        stateMachineChar.process("'\\'", outStream);

        System.out.println();
        System.out.println(stateMachineChar.asDot());
        System.out.println();
    }

}


