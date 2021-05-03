package parser;

import exception.ParserException;
import scanner.Scanner;
import util.Token;
import util.Token.TokenType;
import util.tree.function.ArgList;
import util.tree.expression.Expression;
import util.tree.function.Function;
import util.tree.Program;
import util.tree.statement.*;
import util.tree.unit.Conversion;
import util.tree.unit.Unit;
import util.tree.unit.UnitDeclaration;
import util.tree.unit.CompoundTerm;
import util.tree.unit.CompoundExpr;
import util.tree.function.Argument;

import java.io.IOException;

public class Parser {
    private final Scanner scanner;
    private Token token;

    public Parser(Scanner scanner) throws IOException {
        this.scanner = scanner;
        token = scanner.getNextToken();
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

    private static boolean matchesType(Token token) {
        return token.getTokenType() == TokenType.BASE_TYPE
                || token.getTokenType() == TokenType.BASE_UNIT
                || token.getTokenType() == TokenType.IDENTIFIER;
    }

    private UnitDeclaration parseUnitDeclaration() throws IOException { //todo add simplification if unit expression is of type <a>
        if (!tokenHasType(TokenType.UNIT)) return null;

        token = scanner.getNextToken();
        if (token.getTokenType() == TokenType.IDENTIFIER) {
            String unitName = token.getStringValue();
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
            throwTokenTypeException(TokenType.GREATER);
        }
        return compoundExpr;
    }

    private void throwTokenTypeException(TokenType expected) {
        throw new ParserException(expected, token.getTokenType(), token.getPosition());
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
        Unit unit = new Unit(token.getStringValue());
        token = scanner.getNextToken();
        if (!tokenHasType(TokenType.POWER)) return new CompoundTerm(unit, 1);

        token = scanner.getNextToken();
        if (!tokenHasType(TokenType.INT_LITERAL)) throw new ParserException(TokenType.INT_LITERAL, token);

        int exponent = token.getIntegerValue();
        if (exponent == 0) throw new ParserException("exponent in unit expression cannot be 0", token.getPosition());

        return new CompoundTerm(unit, exponent);
    }

    private static boolean matchesUnitType(Token token) {
        return token.getTokenType() == TokenType.IDENTIFIER
                || token.getTokenType() == TokenType.BASE_UNIT;
    }

    private Conversion parseUnitConversion() {
        return new Conversion();
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
        //token = scanner.getToken();
        if (token.getTokenType() != TokenType.OPEN_BRACKET) {
            throwTokenTypeException(TokenType.OPEN_BRACKET);
        }
        Expression cond = parseExpression();
        if (token.getTokenType() != TokenType.CLOSE_BRACKET) {
            throwTokenTypeException(TokenType.CLOSE_BRACKET);
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

    private ArgList parseArgList() throws IOException {
        if (token.getTokenType() != TokenType.OPEN_BRACKET) {
            throwTokenTypeException(TokenType.OPEN_BRACKET);
        }
        ArgList argList = new ArgList();
        // token = scanner.getToken();
        while (matchesType(token)) {
            argList.add(parseArgument());
            //token = scanner.getToken();
        }
        if (token.getTokenType() != TokenType.CLOSE_BRACKET) {
            throwTokenTypeException(TokenType.CLOSE_BRACKET);
        }
        return argList;
    }

    private Argument parseArgument() throws IOException {
       return null;
    }

    private boolean tokenHasType(TokenType type) {
        return token.getTokenType() == type;
    }
}
