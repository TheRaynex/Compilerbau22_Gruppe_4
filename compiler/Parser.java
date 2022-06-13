package compiler;
import compiler.ast.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private Lexer m_lexer;
    private CompileEnv m_compileEnv;
    private SymbolTable m_symbolTable;
    private FunctionTable m_funcTable;

    public Parser(CompileEnv compileEnv, Lexer lexer) {
        m_compileEnv = compileEnv;
        m_lexer = lexer;
        m_symbolTable = m_compileEnv.getSymbolTable();
        m_funcTable = m_compileEnv.getFunctionTable();
    }

    public SymbolTable getSymbolTable() {
        return m_symbolTable;
    }

    public ASTExprNode parseExpression(String val) throws Exception {
        m_lexer.init(val);
        return getExpr();
    }

    public ASTStmtNode parseStmt(String val) throws Exception {
        m_lexer.init(val);
        return getStmt();
    }

    ASTExprNode getExpr() throws Exception {
        return getQuestionMarkExpr();
    }

    ASTExprNode getParantheseExpr() throws Exception {
        ASTExprNode result = null;
        Token curToken = m_lexer.lookAhead();
        if (curToken.m_type.equals(Token.Type.LPAREN)) {
            m_lexer.expect(Token.Type.LPAREN);
            result = getExpr();
            m_lexer.expect(Token.Type.RPAREN);
            return new ASTParentheseExprNode(result);
        } else if (curToken.m_type.equals(Token.Type.INTEGER)) {
            m_lexer.advance();
            return new ASTIntegerLiteralNode(curToken.m_value);
        } else {
            return getFuncCallExpr();
        }
    }

    // unaryexpr: (NOT | MINUS) ? paranthesisexpr
    ASTExprNode getUnaryExpr() throws Exception {
        var token = m_lexer.lookAhead().m_type;

        if (token == TokenIntf.Type.MINUS || token == TokenIntf.Type.NOT) {
            m_lexer.advance();
            var parenExpr = getParantheseExpr();
            return new ASTUnaryExprNode(parenExpr, token);
        }
        
        return getParantheseExpr();
    }

    /*
    Franziska Ommer, Leon Neumann, Dominik Ochs, Philipp Reichert

    switchcase: SWITCH LPAREN expression RPAREN LBRACE caselist RBRACE
    caselist: case caselist
    caselist: eps | default
    default: DEFAULT COLON blockStmt
    case: CASE literal COLON blockStmt
     */
    ASTStmtNode getSwitchStmt() throws Exception {
        m_lexer.expect(TokenIntf.Type.SWITCH);
        m_lexer.expect(TokenIntf.Type.LPAREN);
        var expr = getExpr();
        m_lexer.expect(TokenIntf.Type.RPAREN);
        m_lexer.expect(TokenIntf.Type.LBRACE);
        var caselist = getCaseListStmt(expr);
        m_lexer.expect(TokenIntf.Type.RBRACE);

        return new ASTSwitchStmtNode(caselist);
    }

    /*
    caselist: case caselist
    caselist: eps | default
     */
    ASTStmtNode getCaseListStmt(ASTExprNode expr) throws Exception {
        var ret = new ASTCaselistStmtNode(expr);

        while (true) {
            var next = m_lexer.lookAhead().m_type;
            switch (next) {
                case RBRACE:
                    return ret;
                case CASE:
                    ret.addCase(getCaseStmt());
                    break;
                case DEFAULT:
                    ret.addCase(getDefaultStmt());
                    if (m_lexer.lookAhead().m_type != TokenIntf.Type.RBRACE)
                        throw new CompilerException("DEFAULT must be the last case in switch", m_lexer.m_currentLineNumber, m_lexer.m_currentLine, "RBRACE");
                    return ret; // switch terminates after first default statement => default must go at the end or be omitted
                default:
                    throw new CompilerException("unexpected token in switch statement", m_lexer.m_currentLineNumber, m_lexer.m_currentLine, "CASE, DEFAULT or RBRACE");
            }
        }
    }

    // default: DEFAULT COLON blockStmt
    ASTCaseDefaultStmtNode getDefaultStmt() throws Exception {
        m_lexer.expect(TokenIntf.Type.DEFAULT);
        m_lexer.expect(TokenIntf.Type.DOUBLECOLON);

        var blockStmt = getBlockStmt();

        return new ASTCaseDefaultStmtNode(blockStmt);
    }

    //CASE literal COLON blockStmt // literal: INTEGER     right now
    ASTCaseStmtNode getCaseStmt() throws Exception {
        m_lexer.expect(TokenIntf.Type.CASE);

        // for now only integer implementation because expression evaluates to integer
        var caseLiteral = m_lexer.lookAhead();
        m_lexer.expect(TokenIntf.Type.INTEGER);

        m_lexer.expect(TokenIntf.Type.DOUBLECOLON);

        var blockStmt = getBlockStmt();

        return new ASTCaseStmtNode(caseLiteral, blockStmt);
    }

    ASTExprNode getMulDivExpr() throws Exception {
        ASTExprNode result = getUnaryExpr();
        Token nextToken = m_lexer.lookAhead();
        while (nextToken.m_type == Token.Type.MUL || nextToken.m_type == Token.Type.DIV) {
            m_lexer.advance();
            result = new ASTMulDivExprNode(result, getUnaryExpr(), nextToken.m_type);
            nextToken = m_lexer.lookAhead();
        }
        return result;
    }

    ASTExprNode getPlusMinusExpr() throws Exception {
        ASTExprNode result = getMulDivExpr();
        Token nextToken = m_lexer.lookAhead();
        while (nextToken.m_type == Token.Type.PLUS || nextToken.m_type == Token.Type.MINUS) {
            m_lexer.advance();
            result = new ASTPlusMinusExprNode(result, getMulDivExpr(), nextToken.m_type);
            nextToken = m_lexer.lookAhead();
        }
        return result;
    }

    ASTExprNode getBitAndOrExpr() throws Exception {
        ASTExprNode result = getPlusMinusExpr();
        Token nextToken = m_lexer.lookAhead();
        while (nextToken.m_type == Token.Type.BITAND || nextToken.m_type == Token.Type.BITOR) {
            if (nextToken.m_type == Token.Type.BITAND) {
                m_lexer.advance();
                result = new ASTBitAndOrExprNode(result, getPlusMinusExpr(), Token.Type.BITAND);
            } else {
                m_lexer.advance();
                result = new ASTBitAndOrExprNode(result, getPlusMinusExpr(), Token.Type.BITOR);
            }
            nextToken = m_lexer.lookAhead();
        }
        return result;
    }

    ASTExprNode getShiftExpr() throws Exception {
        ASTExprNode result = getBitAndOrExpr();
        Token nextToken = m_lexer.lookAhead();
        while(nextToken.m_type == Token.Type.SHIFTLEFT || nextToken.m_type == Token.Type.SHIFTRIGHT){
            m_lexer.advance();
            result = new ASTShiftExprNode(result, getBitAndOrExpr(), nextToken.m_type);
            nextToken = m_lexer.lookAhead();
        }
        return result;
    }

    ASTExprNode getCompareExpr() throws Exception {
        ASTExprNode result = getShiftExpr();
        Token nextToken = m_lexer.lookAhead();
        while (nextToken.m_type == Token.Type.LESS || nextToken.m_type == Token.Type.EQUAL || nextToken.m_type == Token.Type.GREATER) {
            m_lexer.advance();
            result = new ASTCompareExprNode(result, getShiftExpr(), nextToken.m_type);
            nextToken = m_lexer.lookAhead();
        }
        return result;
    }

    ASTExprNode getAndOrExpr() throws Exception {
        ASTExprNode result = getCompareExpr();
        Token nextToken = m_lexer.lookAhead();
        while (nextToken.m_type == Token.Type.AND || nextToken.m_type == Token.Type.OR) {
            m_lexer.advance();
            result = new ASTAndOrExprNode(result, getCompareExpr(), nextToken.m_type);
            nextToken = m_lexer.lookAhead();
        }
        return result;
    }

    ASTExprNode getQuestionMarkExpr() throws Exception {
        ASTExprNode toResolve = getAndOrExpr();
        while (m_lexer.lookAhead().m_type == Token.Type.QUESTIONMARK) {
          m_lexer.expect(Token.Type.QUESTIONMARK);
          ASTExprNode trueNum = getAndOrExpr();
          m_lexer.expect(Token.Type.DOUBLECOLON);
          ASTExprNode falseNum = getAndOrExpr();
          toResolve = new ASTQuestionmarkExprNode(toResolve, trueNum, falseNum);
        }
        return toResolve;
    }
    
    ASTStmtNode getFuncCallStmt() throws Exception {
        ASTFuncCallExprNode callNode = (ASTFuncCallExprNode) getFuncCallExpr();
        m_lexer.expect(Token.Type.SEMICOLON);
        return new ASTFuncCallStmtNode(callNode);
    }
    
    ASTExprNode getFuncCallExpr() throws Exception {
        // CALL keyword is not necessarily needed
        Token keywordToken = m_lexer.lookAhead();
        if (keywordToken.m_type == Token.Type.CALL) {
            // Skip keyword, just syntactic sugar
            m_lexer.advance();
        }
        
        // Check if next token is identifier
        Token identifierToken = m_lexer.lookAhead();
        String identifier = identifierToken.m_value;
        if (identifierToken.m_type != Token.Type.IDENT) {
            throw new Exception(String.format("Lexeme \"%s\" is not an identifier.", identifier));
        }
        m_lexer.advance();
        
        // Read argument list
        Token lParenToken = m_lexer.lookAhead();
        if (lParenToken.m_type != Token.Type.LPAREN) {
            return new ASTVariableExprNode(identifier, getSymbolTable());
        }
        
        m_lexer.advance();
        List<ASTExprNode> params = getArgList();
        m_lexer.expect(Token.Type.RPAREN);
        
        return new ASTFuncCallExprNode(identifier, params);
    }

    // blockstmt: LBRACE stmtlist RBRACE
    // stmtlist: stmt stmtlist
    // stmtlist: epsilon
    ASTStmtNode getBlockStmt() throws Exception {
        ASTBlockStmtNode result = new ASTBlockStmtNode();
        m_lexer.expect(Token.Type.LBRACE);
        while (m_lexer.lookAhead().m_type != Token.Type.RBRACE) {
            result.addStatement(getStmt());
        }
        m_lexer.expect(Token.Type.RBRACE);

        return result;
    }
    
    // block : BLOCK blockstmt
    ASTStmtNode getBlock() throws Exception {
        m_lexer.expect(Token.Type.BLOCK);
        ASTStmtNode content = getBlockStmt();
        ASTBlockNode result = new ASTBlockNode(content);
        return result;
    }
    
    //executeNTimes : EXECUTE expression TIMES block
    ASTStmtNode getExecuteNTimes() throws Exception{
    	m_lexer.expect(Token.Type.EXECUTE);
    	ASTExprNode n = getExpr();
    	m_lexer.expect(Token.Type.TIMES);
    	ASTStmtNode block = getBlockStmt();
    	ASTExecuteNTimesNode result = new ASTExecuteNTimesNode(n,block);
    	return result;
    }
    
    // stmt: declareStmt
    // stmt: assignStmt
    // stmt: printStmt
    // stmt: declareStmt
    // stmt: assignStmt
    // stmt: printStmt
    // stmt: forStmt
    ASTStmtNode getStmt() throws Exception {
        Token token = m_lexer.lookAhead();
        if (token.m_type == Token.Type.DECLARE) {
            return getDeclareStmt();
        } else if (token.m_type == Token.Type.IDENT) {
            return getAssignStmt();
        } else if (token.m_type == Token.Type.PRINT) {
            return getPrintStmt();
        } else if (token.m_type == Token.Type.LBRACE) {
            return getBlockStmt();
        } else if (token.m_type == Token.Type.FUNCTION) {
            return getFuncDefStmt();
        } else if (token.m_type == Token.Type.RETURN) {
            return getReturnStmt();
        } else if (token.m_type == Token.Type.CALL) {
            return getFuncCallStmt();
        } else if (token.m_type == Token.Type.BLOCK) {
            return getBlock();
        } else if (token.m_type == Token.Type.SWITCH) {
            return getSwitchStmt();
        } else if (token.m_type == Token.Type.IF){
            return getIfStmt();
        } else if (token.m_type == Token.Type.FOR){
            return getForStmt();
        }else if (token.m_type == Token.Type.EXECUTE){
            return getExecuteNTimes();
        }
        throw new Exception("Unexpected Statement");
    }

    // declareStmt: DECLARE IDENTIFIER SEMICOLON
    ASTStmtNode getDeclareStmt() throws Exception {
        m_lexer.expect(TokenIntf.Type.DECLARE);

        Token identifier = m_lexer.lookAhead();
        m_lexer.expect(TokenIntf.Type.IDENT);

        m_lexer.expect(TokenIntf.Type.SEMICOLON);

        if(m_symbolTable.getSymbol(identifier.m_value) != null) {
            throw new Exception("Das Symbol \"" + identifier.m_value + "\" ist bereits vergeben!\n");
        }

        m_symbolTable.createSymbol(identifier.m_value);

        return new ASTDeclareNode(m_symbolTable, identifier.m_value);
    }

    // assignStmt: IDENTIFER ASSIGN expr SEMICOLON
    ASTStmtNode getAssignStmt() throws Exception {
        Token nextToken = m_lexer.lookAhead();
        if(m_symbolTable.getSymbol(nextToken.m_value) == null) {
             throw new Exception("Die Variable \"" + nextToken.m_value + "\" ist noch nicht deklariert worden!\n");
         }
        m_lexer.expect(Token.Type.IDENT);
        m_lexer.expect(TokenIntf.Type.ASSIGN);
        ASTStmtNode stmtNode = new ASTAssignStmtNode(getExpr(), m_symbolTable.getSymbol(nextToken.m_value));
        m_lexer.expect(TokenIntf.Type.SEMICOLON);
        return stmtNode;
    }

    // printStmt: PRINT expr SEMICOLON
    ASTStmtNode getPrintStmt() throws Exception {
        m_lexer.expect(TokenIntf.Type.PRINT);
        var node = getExpr();
        m_lexer.expect(TokenIntf.Type.SEMICOLON);
        return new ASTPrintStmtNode(node);
    }

    // variableExpr: IDENTIFIER
    ASTExprNode getVariableExpr() throws Exception {
        Token token = m_lexer.lookAhead();
        if (token.m_type == Token.Type.IDENT){
            m_lexer.advance();
            return new ASTVariableExprNode(token.m_value, getSymbolTable());
        }
        throw new Exception("Unexpected Statement");

    }
    
    ASTStmtNode getReturnStmt() throws Exception {
        m_lexer.expect(Token.Type.RETURN);
        ASTExprNode result = getExpr();
        m_lexer.expect(Token.Type.SEMICOLON);
        return new ASTReturnStmtNode(result);
    }
    
    ASTBlockStmtNode getFuncBody(String identifier) throws Exception {
        ASTBlockStmtNode body = (ASTBlockStmtNode) getBlockStmt();
        List<ASTStmtNode> statements = body.m_statements;
        int size = statements.size();
        
        for (int i = 0; i < size; i++) {
            ASTStmtNode statement = statements.get(i);
            if (i < size - 1) {
                // Statement is return but not at end of block
                if (statement instanceof ASTReturnStmtNode) {
                    throw new Exception(
                            String.format(
                                    "Dead code due to premature return in function \"%s\".", identifier));
                }
            } else if (i == size - 1){
                // Last statement is also not a return statement
                if (!(statement instanceof ASTReturnStmtNode)) {
                    throw new Exception(
                            String.format(
                                    "Return statement missing in function \"%s\".", identifier));
                }
            }
        }
        
        return body;
    }
    
    // func: FUNCTION IDENTIFIER LPAREN paramList RPAREN funcBody
    ASTStmtNode getFuncDefStmt() throws Exception {
        // Read function signature
        m_lexer.expect(Token.Type.FUNCTION);
        
        // Fetch function identifier
        Token identifierToken = m_lexer.lookAhead();
        String identifier = identifierToken.m_value;
        if (identifierToken.m_type != Token.Type.IDENT) {
            throw new Exception(String.format("Lexeme \"%s\" is not an identifier.", identifier));
        }
        m_lexer.advance();
        
        // Check if function already defined
        if (m_funcTable.getFunction(identifier) != null) {
            throw new Exception(String.format("Function \"%s\" already defined.", identifier));
        }
        
        // Read rest of function signature
        m_lexer.expect(Token.Type.LPAREN);
        
        // Read parameter list
        List<String> params = getParamList();
        m_lexer.expect(Token.Type.RPAREN);

        // make entry in function table
        m_funcTable.createFunction(identifier, params);
        
        // Read function body
        ASTBlockStmtNode blockStmtExpr = getFuncBody(identifier);
        return new ASTFuncDefStmtNode(identifier, params, blockStmtExpr);
    }

    // argList: EPSILON
    // argList: expr moreArgs
    // moreArgs: COMMA expr moreArgs
    // moreArgs: EPSILON
    private List<ASTExprNode> getArgList() throws Exception {
        List<ASTExprNode> result = new ArrayList<>();

        boolean expectingExpr = true;
        while (m_lexer.lookAhead().m_type != Token.Type.RPAREN) {
            if (expectingExpr) {
                result.add(getExpr());
            } else {
                m_lexer.expect(Token.Type.COMMA);
            }
            
            // Toggle expectation
            expectingExpr = !expectingExpr;
        }

        return result;
    }
    
    // paramList: EPSILON
    // paramList: IDENTIFIER moreParams
    // moreParams: COMMA IDENTIFIER moreParams
    // moreParams: EPSILON
    private List<String> getParamList() throws Exception {
        List<String> result = new ArrayList<>();
        
        boolean expectingIdent = true;
        while (m_lexer.lookAhead().m_type != Token.Type.RPAREN) {
            if (expectingIdent) {
                ASTVariableExprNode variableNode = (ASTVariableExprNode) getVariableExpr();
                String identifier = variableNode.identifier;
                m_symbolTable.createSymbol(identifier);
                result.add(identifier);
            } else {
                m_lexer.expect(Token.Type.COMMA);
            }
            
            // Toggle expectation
            expectingIdent = !expectingIdent;
        }
        
        return result;
    }
    
    //ifstmt: IF LPAREN condition RPAREN blockstmt elsestmthead
    //condition: expr
    ASTStmtNode getIfStmt() throws Exception {
        m_lexer.expect(TokenIntf.Type.IF);
        m_lexer.expect(TokenIntf.Type.LPAREN);
        ASTExprNode condition = getExpr();
        m_lexer.expect(TokenIntf.Type.RPAREN);
        ASTStmtNode blockstmt = getBlockStmt();
        ASTStmtNode elseblock = getElseStmtHead();
        return new ASTIfNode(condition, blockstmt, elseblock);
    }
    //elsestmthead: ELSE elsebody | EPSILON
    ASTStmtNode getElseStmtHead() throws Exception {
        Token token = m_lexer.lookAhead();
        ASTStmtNode result = null;
        if (token.m_type == TokenIntf.Type.ELSE) {
            m_lexer.advance();
            result = getElseBody();
        }
        return result;
    }
    //elsebody: ifstmt
    //elsebody: blockstmt
    ASTStmtNode getElseBody() throws Exception {
        Token token = m_lexer.lookAhead();
        if (token.m_type == TokenIntf.Type.IF){
            return getIfStmt();
        } else {
            return new ASTElseNode(getBlockStmt());
        }
    }
    // forStmt: FOR LPAREN stmt expr SEMICOLON assignStmt RPAREN blockstmt
    ASTStmtNode getForStmt() throws Exception{
        m_lexer.expect(TokenIntf.Type.FOR);
        m_lexer.expect(TokenIntf.Type.LPAREN);
        ASTStmtNode preStmt = getStmt();
        ASTExprNode condition = getExpr();
        m_lexer.expect(TokenIntf.Type.SEMICOLON);
        ASTStmtNode loopStmt = getStmt();
        m_lexer.expect(TokenIntf.Type.RPAREN);
        ASTStmtNode blockStmt = getBlockStmt();
        return new ASTForNode(preStmt, condition, blockStmt, loopStmt);
    }
}