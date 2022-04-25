package compiler;

import StateMachines.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class Lexer {

    protected Vector<MachineInfo> m_machineList;
    protected String m_input;
    protected List<Token> TokenList = new ArrayList<>();

    public void setUpAllMachines() {
        m_machineList.add(new MachineInfo(new StateMachineChar()));
        m_machineList.add(new MachineInfo(new StateMachineDecimals()));
        m_machineList.add(new MachineInfo(new StateMachineGanzzahl()));
        m_machineList.add(new MachineInfo(new StateMachineIdentifier()));
        m_machineList.add(new MachineInfo(new StateMachineLineComment()));
        m_machineList.add(new MachineInfo(new StateMachineMultiLineComment()));
        m_machineList.add(new MachineInfo(new StateMachineTableAB()));
        m_machineList.add(new MachineInfo(new StateMachineTableStringLiteral()));
        m_machineList.add(new MachineInfo(new StateMachineWhitespaces()));
        //m_machineList.add(new MachineInfo(new StateMachineKeywords()));
    }

    public void process() throws Exception {

        setUpAllMachines();
        while (!m_input.isEmpty()) {

            for (MachineInfo each : m_machineList) {
                each.init(m_input);
            }

            boolean machinesActive = true;
            int currPos = 0;
            do {
                machinesActive = false;
                for (MachineInfo each : m_machineList) {
                    each.m_machine.step();
                    if (!each.m_machine.isFinished()) {
                        machinesActive = true;
                    }
                    if (each.m_machine.isFinalState()) {
                        each.m_acceptPos = currPos;
                    }
                }
                currPos = currPos + 1;
            } while (machinesActive);
            //all Machines are finished, search for longest match
            int highestMatch = -1;
            MachineInfo bestMachine = new MachineInfo(new StateMachineTableAB());
            for (MachineInfo each : m_machineList) {
                if (highestMatch < each.m_acceptPos) {
                    bestMachine = each;
                }
            }
            TokenList.add(new Token(m_input.substring(0, bestMachine.m_acceptPos), bestMachine.m_machine.getName()));
            m_input = m_input.substring(bestMachine.m_acceptPos);

        }
        // while input available
        // while any machine is accepting
        // send next input character to all accepting machines
        // look for first longest match
        // consume match from input and create token
    }

    static class MachineInfo {

        public StateMachineBase m_machine;
        public int m_acceptPos;

        public MachineInfo(StateMachineBase machine) {
            m_machine = machine;
            m_acceptPos = 0;
        }

        public void init(String input) {
            m_acceptPos = 0;
            m_machine.init(input);
        }
    }


    static class Token {
        public String m_word;
        public String m_kind;

        public Token(String word, String kind) {
            m_word = word;
            m_kind = kind;
        }
    }

    public void addMachine(MachineBase machine) {
    }


}