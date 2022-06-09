package compiler;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import machines.StateMachineChar;
import machines.StateMachineDecimals;
import machines.StateMachineGanzzahl;
import machines.StateMachineIdentifier;
import machines.StateMachineKeywords;
import machines.StateMachineLineComment;
import machines.StateMachineMultiLineComment;
import machines.StateMachineStringLiteral;
import machines.StateMachineWhitespaces;

public class Lexer {

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

    protected Vector<MachineInfo> m_machineList;
    protected String m_input;
    protected Token m_currentToken;
    protected String m_currentLine;
    protected int m_currentLineNumber;

    public Lexer() {
        m_machineList = new Vector<MachineInfo>();
        addLexerMachines();
    }

    private void addLexerMachines() {
        compiler.StateMachineBase ganzzahlMachine = new StateMachineGanzzahl();
        addMachine(ganzzahlMachine);
        compiler.StateMachineBase decimalsMachine = new StateMachineDecimals();
        addMachine(decimalsMachine);
        compiler.StateMachineBase stringMachine = new StateMachineStringLiteral();
        addMachine(stringMachine);
        compiler.StateMachineBase charMachine = new StateMachineChar();
        addMachine(charMachine);
        addKeywordMachine("*", compiler.TokenIntf.Type.MUL);
        addKeywordMachine("/", compiler.TokenIntf.Type.DIV);
        addKeywordMachine("+", compiler.TokenIntf.Type.PLUS);
        addKeywordMachine("-", compiler.TokenIntf.Type.MINUS);
        addKeywordMachine("&", compiler.TokenIntf.Type.BITAND);
        addKeywordMachine("|", compiler.TokenIntf.Type.BITOR);
        addKeywordMachine("<<", compiler.TokenIntf.Type.SHIFTLEFT);
        addKeywordMachine(">>", compiler.TokenIntf.Type.SHIFTRIGHT);
        addKeywordMachine("==", compiler.TokenIntf.Type.EQUAL);
        addKeywordMachine("<", compiler.TokenIntf.Type.LESS);
        addKeywordMachine(">", compiler.TokenIntf.Type.GREATER);
        addKeywordMachine("!", compiler.TokenIntf.Type.NOT);
        addKeywordMachine("&&", compiler.TokenIntf.Type.AND);
        addKeywordMachine("||", compiler.TokenIntf.Type.OR);
        addKeywordMachine("?", compiler.TokenIntf.Type.QUESTIONMARK);
        addKeywordMachine(":", compiler.TokenIntf.Type.DOUBLECOLON);
        addKeywordMachine("(", compiler.TokenIntf.Type.LPAREN);
        addKeywordMachine(")", compiler.TokenIntf.Type.RPAREN);
        addKeywordMachine("{", compiler.TokenIntf.Type.LBRACE);
        addKeywordMachine("}", compiler.TokenIntf.Type.RBRACE);
        compiler.StateMachineBase lineCommentMachine = new StateMachineLineComment();
        addMachine(lineCommentMachine);
        compiler.StateMachineBase multiLineCommentMachine = new StateMachineMultiLineComment();
        addMachine(multiLineCommentMachine);
        compiler.StateMachineBase whitespaceMachine = new StateMachineWhitespaces();
        addMachine(whitespaceMachine);
        addKeywordMachine(";", compiler.TokenIntf.Type.SEMICOLON);
        addKeywordMachine("=", compiler.TokenIntf.Type.ASSIGN);

        addKeywordMachine("DECLARE", compiler.TokenIntf.Type.DECLARE);
        addKeywordMachine("PRINT", compiler.TokenIntf.Type.PRINT);
        addKeywordMachine("IF", compiler.TokenIntf.Type.IF);
        addKeywordMachine("ELSE", compiler.TokenIntf.Type.ELSE);
        addKeywordMachine("WHILE", compiler.TokenIntf.Type.WHILE);
        addKeywordMachine("DO", compiler.TokenIntf.Type.DO);
        addKeywordMachine("FOR", compiler.TokenIntf.Type.FOR);
        addKeywordMachine("LOOP", compiler.TokenIntf.Type.LOOP);
        addKeywordMachine("BREAK", compiler.TokenIntf.Type.BREAK);
        addKeywordMachine("SWITCH", compiler.TokenIntf.Type.SWITCH);
        addKeywordMachine("CASE", compiler.TokenIntf.Type.CASE);
        addKeywordMachine("EXECUTE", compiler.TokenIntf.Type.EXECUTE);
        addKeywordMachine("TIMES", compiler.TokenIntf.Type.TIMES);
        addKeywordMachine("FUNCTION", compiler.TokenIntf.Type.FUNCTION);
        addKeywordMachine("CALL", compiler.TokenIntf.Type.CALL);
        addKeywordMachine("RETURN", compiler.TokenIntf.Type.RETURN);
        addKeywordMachine("BLOCK", compiler.TokenIntf.Type.BLOCK);
        addKeywordMachine("DEFAULT", compiler.TokenIntf.Type.DEFAULT);
        
        compiler.StateMachineBase identifierMachine = new StateMachineIdentifier();
        addMachine(identifierMachine);
    }

    public void addKeywordMachine(String keyword, TokenIntf.Type tokenType) {
        m_machineList.add(new MachineInfo(new StateMachineKeywords(keyword, tokenType)));
    }

    public void addMachine(StateMachineBase machine) {
        m_machineList.add(new MachineInfo(machine));
    }

    public void init(String input) throws Exception {
        m_input = input;
        m_currentToken = new Token();
        m_currentLine = new String();
        m_currentLineNumber = 1;
        advance();
    }

    public void initMachines(String input) {
        for (MachineInfo machine : m_machineList) {
            machine.init(input);
        }
    }

    public Token nextWord() throws Exception {
        // check end of file
        if (m_input.isEmpty()) {
            Token token = new Token();
            token.m_type = Token.Type.EOF;
            token.m_value = new String();
            return token;
        }
        int curPos = 0;
        // initialize machines
        initMachines(m_input);
        // while some machine are in process
        boolean machineActive;
        do {
            machineActive = false;
            // for each machine in process
            for (MachineInfo machine : m_machineList) {
                if (machine.m_machine.isFinished()) {
                    continue;
                }
                machineActive = true;
                // next step
                machine.m_machine.step();
                // if possible final state
                if (machine.m_machine.isFinalState()) {
                    // update last position machine would accept
                    machine.m_acceptPos = curPos + 1;
                }
            } // end for each machine in process
            curPos++;
        } while (machineActive); // end while some machine in process
        // select first machine with largest final pos (greedy)
        MachineInfo bestMatch = new MachineInfo(null);
        for (MachineInfo machine : m_machineList) {
            if (machine.m_acceptPos > bestMatch.m_acceptPos) {
                bestMatch = machine;
            }
        }
        // throw in case of error
        if (bestMatch.m_machine == null) {
            throw new CompilerException("Illegal token", m_currentLineNumber, m_currentLine, null);
        }
        // set next word [start pos, final pos)
        String nextWord = m_input.substring(0, bestMatch.m_acceptPos);
        if (nextWord.indexOf('\n') != -1) {
            m_currentLineNumber++;
            m_currentLine = "";
        } else {
            m_currentLine += nextWord;
        }
        m_input = m_input.substring(bestMatch.m_acceptPos);
        Token token = new Token();
        token.m_type = bestMatch.m_machine.getType();
        token.m_value = nextWord;
        return token;
    }

    public Token nextToken() throws Exception {
        Token token = nextWord();
        while (token.m_type == Token.Type.WHITESPACE ||
                token.m_type == Token.Type.MULTILINECOMMENT ||
                token.m_type == Token.Type.LINECOMMENT) {
            token = nextWord();
        }
        return token;
    }

    boolean isWhitespace(char c) {
        if (c == ' ' || c == '\t' || c == '\n') {
            return true;
        } else {
            return false;
        }
    }

    void skipWhiteSpace() {
        int i = 0;
        while (i < m_input.length() && isWhitespace(m_input.charAt(i))) {
            i++;
        }
        m_input = m_input.substring(i);
    }

    public void processInput(String input, OutputStreamWriter outStream) throws Exception {
        m_input = input;
        m_currentLine = "";
        m_currentLineNumber = 1;
        // while input available
        while (!m_input.isEmpty()) {
            // get next word
            Token curWord = nextWord();
            // break on failure
            if (curWord.m_type == Token.Type.EOF) {
                outStream.write("ERROR\n");
                outStream.flush();
                break;
            } else if (curWord.m_type == Token.Type.WHITESPACE) {
                continue;
            } else {
                // print word
                outStream.write(curWord.toString());
                outStream.write("\n");
                outStream.flush();
            }
        }
    }

    public Token lookAhead() {
        return m_currentToken;
    }

    public void advance() throws Exception {
        m_currentToken = nextToken();
    }

    public void expect(Token.Type tokenType) throws Exception {
        if (tokenType == m_currentToken.m_type) {
            advance();
        } else {
            throw new CompilerException(
                    "Unexpected token " + m_currentToken.toString(),
                    m_currentLineNumber, m_currentLine,
                    Token.type2String(tokenType));
        }
    }

    public boolean accept(Token.Type tokenType) throws Exception {
        if (tokenType == m_currentToken.m_type) {
            advance();
            return true;
        }
        return false;
    }
}
