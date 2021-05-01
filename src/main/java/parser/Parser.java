package parser;

import exception.ParserException;
import org.jetbrains.annotations.VisibleForTesting;
import scanner.Scanner;
import util.Token;
import util.Token.TokenType;
import util.tree.Expression;
import util.tree.Function;
import util.tree.Program;
import util.tree.statement.*;
import util.tree.unit.Conversion;
import util.tree.unit.Unit;
import util.tree.unit.UnitDeclaration;
import util.tree.unit.compound.CompoundTerm;
import util.tree.unit.compound.CompoundExpr;

import java.io.IOException;

public class Parser {
    private final Scanner scanner;
    private Token token;

    public Parser (Scanner scanner) throws IOException {
        this.scanner = scanner;
    }

    public Program parse() throws IOException {
        Program program = new Program();
        token = scanner.getToken();
        while(token.getTokenType() == TokenType.UNIT || token.getTokenType() == TokenType.LET){
            if(token.getTokenType() == TokenType.UNIT){
                    UnitDeclaration u = parseUnitDeclaration();
                    program.add(u);
            }else{      // case LET
                    Conversion c = parseUnitConversion();
                    program.add(c);
            }
        }
        while (isType(token)){
            Function fun = parseFunction();
            program.add(fun);
        }

        // handle unexpected tokens
        if (token.getTokenType()!= TokenType.EOT){
            throw new ParserException("function definition", token.getTokenType(), token.getPosition());
        }

        return program;
    }

    private static boolean isType(Token token){
        return token.getTokenType() == TokenType.BASE_TYPE
                || token.getTokenType() == TokenType.BASE_UNIT
                || token.getTokenType() == TokenType.IDENTIFIER;
    }

    private UnitDeclaration parseUnitDeclaration() throws IOException {
        token = scanner.getToken();
        if(token.getTokenType() == TokenType.IDENTIFIER){
            String unitName = token.getStringValue();
            token = scanner.getToken();
            CompoundExpr type = null;
            if(token.getTokenType() == TokenType.AS){
                type = parseCompoundExpression();
            }
            if(token.getTokenType() == TokenType.SEMICOLON){
                return new UnitDeclaration(unitName, type);
            }else{
                throwTokenTypeException(TokenType.SEMICOLON);
            }
        }
        throw new ParserException(TokenType.IDENTIFIER, token.getTokenType(), token.getPosition());
    }

    private CompoundExpr parseCompoundExpression() throws IOException{
        token = scanner.getToken();
        //check opening token
        if (token.getTokenType() != TokenType.LESS){
            throwTokenTypeException(TokenType.LESS);
        }
        //must have at least one compound term
        CompoundExpr compoundExpr = new CompoundExpr();
        parseCompoundNumerator(compoundExpr);

        if(token.getTokenType() == TokenType.DIVIDE){
            parseCompoundDenominator(compoundExpr);
        }

        //check if compound expression has a proper ending
        if(token.getTokenType() != TokenType.GREATER){
            throwTokenTypeException(TokenType.GREATER);
        }
        token = scanner.getToken();
        if(token.getTokenType() != TokenType.SEMICOLON){
            throwTokenTypeException(TokenType.SEMICOLON);
        }

        return compoundExpr;
    }

    private void throwTokenTypeException (TokenType expected){
        throw  new ParserException(expected, token.getTokenType(), token.getPosition());
    }

    private void parseCompoundNumerator (CompoundExpr expr) throws IOException{
        parseCompoundTerms(expr, false);
    }

    private void parseCompoundDenominator (CompoundExpr expr) throws IOException{
        parseCompoundTerms(expr, true);
    }

    private void parseCompoundTerms(CompoundExpr expr, boolean isDenominator) throws IOException {
            CompoundTerm one = parseOneCompoundTerm();
            expr.addPart(one);
            token = scanner.getToken();
            while (token.getTokenType() == TokenType.MULTIPLY){
                CompoundTerm term = parseOneCompoundTerm();
                if (isDenominator) term.negate();
                expr.addPart(term);
                token = scanner.getToken();
            }
    }

    private CompoundTerm parseOneCompoundTerm () throws IOException {
        token = scanner.getToken();
        if(!isUnitType()){
            throw new ParserException("unit type", token.getTokenType(), token.getPosition());
        }
        Unit unit = new Unit(token.getStringValue());
        int exponent = 1;
        token = scanner.getToken();
        if (token.getTokenType() == TokenType.POWER){
            token = scanner.getToken();
            if (token.getIntegerValue()== 0) {
                throw new ParserException("exponent in unit expression cannot be 0", token.getPosition());
            }
            exponent = token.getIntegerValue();
        }
        return new CompoundTerm(unit, exponent);
    }

    private boolean isUnitType (){
        return token.getTokenType() == TokenType.IDENTIFIER || token.getTokenType() == TokenType.BASE_UNIT;
    }



    private Conversion parseUnitConversion() {
        return new Conversion();
    }

    private Function parseFunction(){
        return  new Function();
    }

    private Statement parseStatements() throws IOException {
        token = scanner.getToken();
        if (token.getTokenType()==TokenType.CURLY_OPEN){
            return parseBlockStatement();
        }else{
            return parseOneStatement();
        }
    }

    private BlockStatement parseBlockStatement() throws IOException{
        return null;
    }

    private Statement parseOneStatement() throws IOException{
        return switch (token.getTokenType()) {
            case RETURN -> parseReturn();
            case WHILE -> parseLoop();
            case BREAK -> parseBreak();
            case CONTINUE -> parseContinue();
            default -> null;
        };
    }

    private WhileStatement parseLoop() throws IOException {
        token = scanner.getToken();
        if (token.getTokenType() != TokenType.OPEN_BRACKET){
            throwTokenTypeException(TokenType.OPEN_BRACKET);
        }
        Expression cond = parseExpression();
        if (token.getTokenType() != TokenType.CLOSE_BRACKET){
            throwTokenTypeException(TokenType.CLOSE_BRACKET);
        }
        Statement body = parseStatements();
        return new WhileStatement(body, cond);
    }

    private ReturnStatement parseReturn() throws IOException{
        token = scanner.getToken();
        Expression expr = null;

        //build value if exists
        if(token.getTokenType() != TokenType.SEMICOLON){
            expr = parseExpression();
        }
        if (token.getTokenType() != TokenType.SEMICOLON){
            throwTokenTypeException(TokenType.SEMICOLON);
        }
        return new ReturnStatement(expr);

    }

    private BreakStatement parseBreak() throws IOException{
        token = scanner.getToken();
        if (token.getTokenType() != TokenType.SEMICOLON){
            throwTokenTypeException(TokenType.SEMICOLON);
        }
        return new BreakStatement();
    }

    private ContinueStatement parseContinue() throws IOException{
        token = scanner.getToken();
        if (token.getTokenType() != TokenType.SEMICOLON){
            throwTokenTypeException(TokenType.SEMICOLON);
        }
        return new ContinueStatement();
    }

    private Expression parseExpression () throws IOException{
        return null;
    }
}
