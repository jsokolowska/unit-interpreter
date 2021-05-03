package parser;

import exception.ParserException;
import exception.ScannerException;
import scanner.Scanner;
import util.Token;
import util.Token.TokenType;
import util.tree.Program;
import util.tree.expression.Expression;
import util.tree.function.Arguments;
import util.tree.function.Function;
import util.tree.function.Parameters;
import util.tree.statement.*;
import util.tree.type.TypeManager;
import util.tree.type.UnitType;
import util.tree.unit.*;

import java.io.IOException;

public class Parser {
    private final Scanner scanner;
    private final TypeManager typeManager = new TypeManager();
    private Token token;

    public Parser(Scanner scanner) throws IOException {
        this.scanner = scanner;
        token = scanner.getNextToken();
    }

    private static boolean matchesType(Token token) {
        return token.isBaseType() || token.isBaseUnit() || token.getTokenType() == TokenType.IDENTIFIER;
    }

    private static boolean matchesUnitType(Token token) {
        return token.getTokenType() == TokenType.IDENTIFIER || token.isBaseUnit();
    }

    public Program parse() throws IOException {
        Program program = new Program();

        // if you can parse unit declaration or conversion
        boolean parsed = true;
        while (parsed) {
            parsed = false;
            UnitDeclaration unitDeclaration = parseUnitDeclaration();
            Conversion conversion = parseUnitConversion();
            if (unitDeclaration != null) {
                parsed = true;
                program.add(unitDeclaration);
            } else if (conversion != null) {
                parsed = true;
                program.add(conversion);
            }
        }

        // try parsing functions until you get to the end of file
        while (token.getTokenType() != TokenType.EOT) {
            Function function = parseFunction();
            program.add(function);
        }

        if (!program.hasFunctions()) {
            throw new ParserException("Progam needs to have at least one function");
        }

        return program;
    }

    private UnitDeclaration parseUnitDeclaration() throws IOException { //todo add simplification if unit expression is of type <a>
        if (!tokenHasType(TokenType.UNIT)) return null;

        token = scanner.getNextToken();
        if (token.getTokenType() == TokenType.IDENTIFIER) {
            String unitName = token.getStringValue();
            if (typeManager.exists(unitName)) {
                throw new ParserException("Unit redefinition not allowed", token.getPosition());
            }

            token = scanner.getNextToken();
            CompoundExpr type = null;

            if (token.getTokenType() == TokenType.AS) {
                token = scanner.getNextToken();
                type = parseCompoundExpression();
                if (type == null) {
                    throw new ParserException(TokenType.LESS, token);
                }
                token = scanner.getNextToken();
            }
            if (token.getTokenType() == TokenType.SEMICOLON) {
                return new UnitDeclaration(unitName, type);
            } else {
                throw new ParserException(TokenType.SEMICOLON, token);
            }
        }
        throw new ParserException(TokenType.IDENTIFIER, token);
    }

    private CompoundExpr parseCompoundExpression() throws IOException {
        if (token.getTokenType() != TokenType.LESS) return null;

        token = scanner.getNextToken();
        CompoundExpr compoundExpr = new CompoundExpr();
        parseCompoundNumerator(compoundExpr); //never returns null


        if (token.getTokenType() == TokenType.DIVIDE) {
            token = scanner.getNextToken();
            parseCompoundDenominator(compoundExpr);
        }

        //check if compound expression has a proper ending
        if (token.getTokenType() != TokenType.GREATER) {
            throw new ParserException(TokenType.GREATER, token);
        }
        return compoundExpr;
    }

    private void parseCompoundNumerator(CompoundExpr expr) throws IOException {
        parseCompoundTerms(expr, false);
    }

    private void parseCompoundDenominator(CompoundExpr expr) throws IOException {
        parseCompoundTerms(expr, true);
    }

    private void parseCompoundTerms(CompoundExpr expr, boolean isDenominator) throws IOException {
        // will always return a term or throw ParserException
        CompoundTerm one = parseOneCompoundTerm();
        expr.addPart(one);
        token = scanner.getNextToken();

        while (tokenHasType(TokenType.MULTIPLY)) {
            token = scanner.getNextToken();
            CompoundTerm term = parseOneCompoundTerm();
            if (isDenominator) term.negate();
            expr.addPart(term);
            token = scanner.getNextToken();
        }

    }

    private CompoundTerm parseOneCompoundTerm() throws IOException {
        if (!matchesUnitType(token)) {
            throw new ParserException("unit type", token);
        }
        UnitType unit = typeManager.getUnitType(token);
        if(unit == null) throw new ParserException("Unit usage before definition", token.getPosition());

        token = scanner.getNextToken();
        if (!tokenHasType(TokenType.POWER)) return new CompoundTerm(unit, 1);

        token = scanner.getNextToken();
        if (!tokenHasType(TokenType.INT_LITERAL)) throw new ParserException(TokenType.INT_LITERAL, token);

        int exponent = token.getIntegerValue();
        if (exponent == 0) throw new ParserException("exponent in unit expression cannot be 0", token.getPosition());

        return new CompoundTerm(unit, exponent);
    }

    private Conversion parseUnitConversion() throws IOException {
        if(!tokenHasType(TokenType.LET)) return null;

        token = scanner.getNextToken();
        if(!tokenHasType(TokenType.IDENTIFIER)){
            throw new ParserException(TokenType.IDENTIFIER, token);
        }
        String identifier = token.getStringValue();
        token = scanner.getNextToken();
        Parameters parameters = parseParameters();

        if (parameters == null){
            throw new ParserException("Expected parameters", token.getPosition());
        }else{
            token = scanner.getNextToken();
            ConversionFunction conversionFunction = parseConversionFunction();
            if (conversionFunction != null){
                return new Conversion(identifier, parameters, conversionFunction);
            }else{
                throw new ParserException("Expected function body ", token.getPosition());
            }
        }
    }

    private Parameters parseParameters () throws IOException {
        if(!tokenHasType(TokenType.OPEN_BRACKET)) return null;

        token = scanner.getNextToken();

        if(!tokenHasType(TokenType.IDENTIFIER)) return null;


        return new Parameters();
    }



    private ConversionFunction parseConversionFunction(){
        return new ConversionFunction();
    }

    private Function parseFunction() {
        return new Function();
    }

    private Statement parseStatements() throws IOException {
        Statement st = parseBlockStatement();
        if (st != null) return st;
        return st = parseOneStatement();
    }

    private BlockStatement parseBlockStatement() throws IOException {
        return null;
    }

    private Statement parseOneStatement() throws IOException {
        return switch (token.getTokenType()) {
            case RETURN -> parseReturn();
            case WHILE -> parseLoop();
            case BREAK -> parseBreak();
            case CONTINUE -> parseContinue();
            default -> null;
        };
    }

    private WhileStatement parseLoop() throws IOException {
        if (token.getTokenType() != TokenType.OPEN_BRACKET) {
            throw new ParserException(TokenType.OPEN_BRACKET, token);
        }
        Expression cond = parseExpression();
        if (token.getTokenType() != TokenType.CLOSE_BRACKET) {
            throw new ParserException(TokenType.CLOSE_BRACKET, token);
        }
        Statement body = parseStatements();
        return new WhileStatement(body, cond);
    }

    private ReturnStatement parseReturn() throws IOException {
        if (!tokenHasType(TokenType.RETURN)) return null;

        token = scanner.getNextToken();
        Expression expr;
        expr = parseExpression();
        //build value if exists
        if (tokenHasType(TokenType.SEMICOLON)) {
            return new ReturnStatement(expr);
        }
        throw new ParserException(TokenType.SEMICOLON, token);
    }

    private BreakStatement parseBreak() throws IOException { //todo refactor maybe into one function?
        if (!tokenHasType(TokenType.BREAK)) return null;

        token = scanner.getNextToken();
        if (tokenHasType(TokenType.SEMICOLON)) {
            return new BreakStatement();
        }
        throw new ParserException(TokenType.SEMICOLON, token);
    }

    private ContinueStatement parseContinue() throws IOException {
        if (!tokenHasType(TokenType.CONTINUE)) return null;

        token = scanner.getNextToken();
        if (tokenHasType(TokenType.SEMICOLON)) {
            return new ContinueStatement();
        }
        throw new ParserException(TokenType.SEMICOLON, token);
    }

    private Expression parseExpression() throws IOException {
        return null;
    }

    private Arguments parseArgument() throws IOException {
        return null;
    }

    private boolean tokenHasType(TokenType type) {
        return token.getTokenType() == type;
    }
}
