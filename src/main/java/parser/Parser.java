package parser;

import exception.ParserException;
import exception.ScannerException;
import scanner.Scanner;
import util.Token;
import util.Token.TokenType;
import util.tree.Program;
import util.tree.expression.Expression;
import util.tree.expression.math.*;
import util.tree.expression.operator.Operator;
import util.tree.expression.operator.OperatorFactory;
import util.tree.expression.operator.PowerOperator;
import util.tree.expression.unit.*;
import util.tree.function.*;
import util.tree.statement.*;
import util.tree.type.Type;
import util.tree.type.TypeManager;
import util.tree.type.UnitType;
import util.tree.unit.*;
import util.tree.value.FunctionCall;
import util.tree.value.VariableValue;
import util.tree.value.literal.*;

import java.io.IOException;

public class Parser {
    private final Scanner scanner;
    private final TypeManager typeManager = new TypeManager();
    private Token token;

    public Parser(Scanner scanner) throws IOException {
        this.scanner = scanner;
        token = scanner.getToken();
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
            throw new ParserException("Program needs to have at least one function");
        }

        return program;
    }

    private UnitDeclaration parseUnitDeclaration() throws IOException { //todo add simplification if unit expression is of type <a>
        if (!tokenHasType(TokenType.UNIT)) return null;

        token = scanner.getToken();
        if (token.getTokenType() == TokenType.IDENTIFIER) {
            String unitName = token.getStringValue();
            if (typeManager.exists(unitName)) {
                throw new ParserException("Unit redefinition not allowed", token.getPosition());
            }

            token = scanner.getToken();
            CompoundExpr type = null;

            if (token.getTokenType() == TokenType.AS) {
                token = scanner.getToken();
                type = parseCompoundExpression();
                if (type == null) {
                    throw new ParserException(TokenType.LESS, token);
                }
                token = scanner.getToken();
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

        token = scanner.getToken();
        CompoundExpr compoundExpr = new CompoundExpr();
        parseCompoundNumerator(compoundExpr); //never returns null


        if (token.getTokenType() == TokenType.DIVIDE) {
            token = scanner.getToken();
            parseCompoundDenominator(compoundExpr);
        }

        //check if compound expression has a proper ending
        if (token.getTokenType() != TokenType.GREATER) {
            throw new ParserException(TokenType.GREATER, token);
        }
        compoundExpr.simplify();
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
        if (isDenominator) one.negate();
        expr.addPart(one);
        token = scanner.getToken();

        while (tokenHasType(TokenType.MULTIPLY)) {
            token = scanner.getToken();
            CompoundTerm term = parseOneCompoundTerm();
            if (isDenominator) term.negate();
            expr.addPart(term);
            token = scanner.getToken();
        }

    }

    private CompoundTerm parseOneCompoundTerm() throws IOException {
        if (!matchesUnitType(token)) {
            throw new ParserException("unit type", token);
        }
        UnitType unit = typeManager.getUnitType(token);
        if(unit == null) throw new ParserException("Unit usage before definition", token.getPosition());

        token = scanner.getToken();
        if (!tokenHasType(TokenType.POWER)) return new CompoundTerm(unit, 1);

        token = scanner.getToken();
        if (!tokenHasType(TokenType.INT_LITERAL)) throw new ParserException(TokenType.INT_LITERAL, token);

        int exponent = token.getIntegerValue();
        if (exponent == 0) throw new ParserException("exponent in unit expression cannot be 0", token.getPosition());

        return new CompoundTerm(unit, exponent);
    }

    private Conversion parseUnitConversion() throws IOException {
        if(!tokenHasType(TokenType.LET)) return null;

        token = scanner.getToken();
        if(! matchesUnitType(token)){
            throw new ParserException("Expected unit type", token);
        }
        UnitType unit = typeManager.getUnitType(token);
        if(unit == null) {
            throw new ParserException("Unit usage before definition", token.getPosition());
        }
        token = scanner.getToken();

        Parameters parameters = parseParameters();
        if (parameters == null){
            throw new ParserException(TokenType.OPEN_BRACKET, token);
        }else{
            token = scanner.getToken();
            if (!tokenHasType(TokenType.CURLY_OPEN)){
                throw new ParserException(TokenType.CURLY_OPEN, token);
            }
            token = scanner.getToken();

            Expression conversionExpression = parseConversionExpression();
            if (conversionExpression == null){
                throw new ParserException("Expected function body ", token);
            }

            token = scanner.getToken();
            if(! tokenHasType(TokenType.CURLY_CLOSE)){
                throw new ParserException(TokenType.CURLY_CLOSE, token);
            }
            return new Conversion(unit, parameters, conversionExpression);

        }
    }

    private Parameters parseParameters () throws IOException {
        if(!tokenHasType(TokenType.OPEN_BRACKET)) return null;

        token = scanner.getToken();
        Parameters params = new Parameters();
        //if after open bracket there is immediately close bracket parameter list is empty
        if(tokenHasType(TokenType.CLOSE_BRACKET)) return params;

        parseParameter(params);
        token = scanner.getToken();
        while(tokenHasType(TokenType.COMMA)){
            token = scanner.getToken();
            parseParameter(params);
            token = scanner.getToken();

        }
        if(!tokenHasType(TokenType.CLOSE_BRACKET)){
            throw new ParserException(TokenType.CLOSE_BRACKET, token);
        }
        return params;
    }

    private void parseParameter (Parameters parameters) throws IOException {
        if (!matchesType(token)) {
            throw new ParserException("Expected type", token);
        }
        Type type = typeManager.getType(token);
        if (type == null) {
            throw new ParserException("Type usage before definition", token.getPosition());
        }
        token = scanner.getToken();
        if (!tokenHasType(TokenType.IDENTIFIER)){
            throw new ParserException(TokenType.IDENTIFIER, token);
        }
        parameters.addParameter(token.getStringValue(), type);
    }

    private Expression parseConversionExpression() throws IOException {
        Expression expressionPart = parseMulUnitExpression();
        if (expressionPart == null) return null;

        ConversionExpression expression = new ConversionExpression();
        expression.add(expressionPart);

        while(tokenHasType(TokenType.PLUS) || tokenHasType(TokenType.MINUS) ){
            Operator op = OperatorFactory.getAdditiveOperator(token.getTokenType());
            token = scanner.getToken();
            expressionPart = parseMulUnitExpression();
            if (expressionPart == null){
                throw new ParserException("Expected expression", token.getPosition());
            }
            expression.add(expressionPart, op);
        }

        if(expression.size()>1){
            return expression;
        }
        return expressionPart;
    }

    private Expression parseMulUnitExpression () throws IOException {
        Expression expressionPart = parsePowerUnitExpression();
        if (expressionPart == null) return null;

        MulUnitExpression expression = new MulUnitExpression();
        expression.add(expressionPart);

        while(tokenHasType(TokenType.DIVIDE) || tokenHasType(TokenType.MULTIPLY) ){
            Operator op = OperatorFactory.getOperator(token);
            token = scanner.getToken();
            expressionPart = parsePowerUnitExpression();
            if (expressionPart == null){
                throw new ParserException("Expected expression", token.getPosition());
            }
            expression.add(expressionPart, op);
        }

        if(expression.size()>1){
            return expression;
        }
        return expressionPart;
    }

    private Expression parsePowerUnitExpression () throws IOException {
        Expression expressionPart = parseUnaryUnitExpression();
        PowerUnitExpression expression = new PowerUnitExpression();
        expression.add(expressionPart);

        token = scanner.getToken();
        while(tokenHasType(TokenType.POWER) ){
            //Operator op = OperatorFactory.getOperator(token);
            token = scanner.getToken();
            expressionPart = parseUnaryUnitExpression();
            expression.add(expressionPart);
        }
        if(expression.size()>1){
            return expression;
        }
        return expressionPart;
    }

    private Expression parseUnaryUnitExpression () throws IOException {
        if(tokenHasType(TokenType.MINUS)){
            token = scanner.getToken();
            UnaryUnitExpression expr = new UnaryUnitExpression();
            Expression expressionPart = parseUnitExpression();
            if(expressionPart == null){
                throw new ParserException("Expected expression", token.getPosition());
            }
            expr.add(expressionPart);
            return expr;
        }
        Expression expressionPart = parseUnitExpression();
        if(expressionPart == null){
            throw new ParserException("Expected expression", token.getPosition());
        }
        return expressionPart;
    }

    private Expression parseUnitExpression () throws IOException {
        if(tokenHasType(TokenType.OPEN_BRACKET)){
            token = scanner.getToken();
            Expression expr = parseConversionExpression();
            if(expr == null){
                throw new ParserException("Expected expression", token.getPosition());
            }
            if(!tokenHasType(TokenType.CLOSE_BRACKET)){
                throw new ParserException(TokenType.CLOSE_BRACKET, token);
            }
            return expr;
        }
        Expression value;
        if((value = parseNumberLiteral()) != null) return value;
        if((value = parseVariableValue()) != null) return value;
        return null;
    }

    private Literal parseNumberLiteral (){
        return switch (token.getTokenType()){
            case INT_LITERAL -> new IntLiteral(token.getIntegerValue());
            case FLOAT_LITERAL -> new FloatLiteral(token.getDoubleValue());
            default -> null;
        };
    }

    private VariableValue parseVariableValue (){
        if(!tokenHasType(TokenType.IDENTIFIER)) return null;
        return new VariableValue(token.getStringValue());
    }

    private Literal parseLiteral (){
        return switch (token.getTokenType()){
            case STRING_LITERAL -> new StringLiteral(token.getStringValue());
            case BOOL_LITERAL -> new BoolLiteral(token.getBoolValue());
            case INT_LITERAL -> new IntLiteral(token.getIntegerValue());
            case FLOAT_LITERAL -> new FloatLiteral(token.getDoubleValue());
            default -> null;
        };
    }

    private Function parseFunction() throws IOException {
        Type type = TypeManager.getUnitType(token);
        if(type == null){
            if((type = TypeManager.getType(token))==null){
                if(tokenHasType(TokenType.IDENTIFIER)){
                    throw new ParserException("Type usage before definition", token.getPosition());
                }
            }
        }
        token = scanner.getToken();
        if(!tokenHasType(TokenType.IDENTIFIER)){
            throw new ParserException(TokenType.IDENTIFIER, token);
        }
        String identifier = token.getStringValue();
        token = scanner.getToken();
        Parameters params = parseParameters();
        BlockStatement statement = parseBlockStatement();
        if(statement == null) throw new ParserException(TokenType.CURLY_OPEN, token);

        return new Function(identifier, statement, params, type);
    }

    private Statement parseStatement() throws IOException {
        Statement st ;
        if ((st = parseBlockStatement() )!= null) return st;
        if ((st = parseReturn() )!= null) return st;
        if ((st = parseBreak())!= null) return st;
        if ((st = parseContinue())!= null) return st;
        if ((st = parseLoop() )!= null) return st;
        if ((st = parseIfElseStatement() )!= null) return st;
        if ((st = parseCallStatement() )!= null) return st;
        if ((st = parsePrintStatement())!= null) return st;
        if ((st = parseExplainStatement() )!= null) return st;
        if ((st = parseAssignStatement() )!= null) return st;
        if ((st = parseVariableDeclarationStatement() )!= null) return st;
        if ((st = parseTypeStatement() )!= null) return st;
        return null;
    }

    private IfElseStatement parseIfElseStatement () throws IOException {
        if(!tokenHasType(TokenType.IF)) return null;
        token = scanner.getToken();
        if(!tokenHasType(TokenType.OPEN_BRACKET)){
            throw new ParserException(TokenType.OPEN_BRACKET, token);
        }
        token = scanner.getToken();
        Expression ifCondition = parseExpression();
        if(ifCondition == null) throw new ParserException("Expected condition");
        if(!tokenHasType(TokenType.CLOSE_BRACKET)){
            throw new ParserException(TokenType.CLOSE_BRACKET, token);
        }
        Statement ifBody = parseStatement();
        if(ifBody == null){
            throw new ParserException("Empty if body", token.getPosition());
        }
        if(tokenHasType(TokenType.ELSE)){
            token = scanner.getToken();
        }
        return new IfElseStatement();
    }

    private CallStatement parseCallStatement (){
        return null;
    }

    private PrintStatement parsePrintStatement (){
        return null;
    }

    private ExplainStatement parseExplainStatement (){
        return null;
    }

    private AssignStatement parseAssignStatement (){
        return  null;
    }

    private VariableDeclarationStatement parseVariableDeclarationStatement(){
        return null;
    }

    private TypeStatement parseTypeStatement (){
        return null;
    }

    private BlockStatement parseBlockStatement() throws IOException {
        if(!tokenHasType(TokenType.CURLY_OPEN)) return null;
        token = scanner.getToken();
        BlockStatement block = new BlockStatement();
        Statement stmt;
        while(true){
            if((stmt = parseStatement()) != null){
                block.add(stmt);
                token = scanner.getToken();
            }else{
                break;
            }
        }
        if(!tokenHasType(TokenType.CURLY_CLOSE)){
            throw new ParserException(TokenType.CURLY_CLOSE, token);
        }
        return block;
    }

    private WhileStatement parseLoop() throws IOException {
        if (!tokenHasType(TokenType.WHILE)) return null;
        token = scanner.getToken();

        if(!tokenHasType(TokenType.OPEN_BRACKET)) {
            throw new ParserException(TokenType.OPEN_BRACKET, token);
        }
        token = scanner.getToken();
        Expression cond = parseOrExpression();
        if (cond == null){
            throw  new ParserException("Expected conditional expression", token.getPosition());
        }
        token = scanner.getToken();
        Statement body = parseStatement();
        if (body == null){
            throw new ParserException(TokenType.CURLY_OPEN, token);
        }
        return new WhileStatement(body, cond);
    }

    private ReturnStatement parseReturn() throws IOException {
        if (!tokenHasType(TokenType.RETURN)) return null;

        token = scanner.getToken();
        //build value if exists
        if (tokenHasType(TokenType.SEMICOLON)) {
            return new ReturnStatement(null);
        }else{
            Expression expr;
            expr = parseOrExpression();
            if(tokenHasType(TokenType.SEMICOLON)){
                return new ReturnStatement(expr);
            }
        }
        throw new ParserException(TokenType.SEMICOLON, token);
    }

    private BreakStatement parseBreak() throws IOException { //todo refactor maybe into one function?
        if (!tokenHasType(TokenType.BREAK)) return null;

        token = scanner.getToken();
        if (tokenHasType(TokenType.SEMICOLON)) {
            return new BreakStatement();
        }
        throw new ParserException(TokenType.SEMICOLON, token);
    }

    private ContinueStatement parseContinue() throws IOException {
        if (!tokenHasType(TokenType.CONTINUE)) return null;

        token = scanner.getToken();
        if (tokenHasType(TokenType.SEMICOLON)) {
            return new ContinueStatement();
        }
        throw new ParserException(TokenType.SEMICOLON, token);
    }

    private Expression parseOrExpression() throws IOException {
        OrExpression expression = new OrExpression();
        Expression expressionComponent = parseAndExpression();
        if (expressionComponent == null) return  null;
        expression.add(expressionComponent);
        //token = scanner.getToken();
        while (tokenHasType(TokenType.OR)){
            token = scanner.getToken();
            expressionComponent = parseAndExpression();
            expression.add(expressionComponent);
        }
        if(expression.size() == 1)  return expressionComponent;
        return expression;
    }

    private Expression parseAndExpression() throws IOException {
        AndExpression expression = new AndExpression();
        Expression expressionComponent = parseComparisonExpression();
        if(expressionComponent == null) return null;
        expression.add(expressionComponent);

        while(tokenHasType(TokenType.AND)){
            token = scanner.getToken();
            expressionComponent = parseComparisonExpression();
            expression.add(expressionComponent);
        }
        if(expression.size()==1) return expressionComponent;
        return expression;
    }

    private Expression parseComparisonExpression ()throws IOException {
        Expression expressionPart = parseRelationalExpression();
        if(expressionPart == null) return null;
        ComparisonExpression expr = new ComparisonExpression();
        expr.add(expressionPart);

        while(tokenHasType(TokenType.EQUAL) || tokenHasType(TokenType.NOT_EQUAL)){
            Operator op = OperatorFactory.getOperator(token);
            token = scanner.getToken();
            expressionPart  = parseRelationalExpression();
            if (expressionPart == null){
                throw new ParserException("Expected expression", token.getPosition());
            }
            expr.add(expressionPart, op);
        }
        if(expr.size()==1){
            return expressionPart;
        }
        return expr;
    }

    private Expression parseRelationalExpression ()throws IOException {
        Expression expressionPart = parseArithmeticExpression();
        if(expressionPart == null) return null;
        RelationalExpression expr = new RelationalExpression();
        expr.add(expressionPart);

        while(isRelOp(token)){
            Operator op = OperatorFactory.getOperator(token);
            token = scanner.getToken();
            expressionPart  = parseArithmeticExpression();
            if (expressionPart == null){
                throw new ParserException("Expected expression", token.getPosition());
            }
            expr.add(expressionPart, op);
        }
        if(expr.size()==1){
            return expressionPart;
        }
        return expr;
    }

    private static boolean isRelOp(Token token){
        return token.getTokenType() == TokenType.GREATER || token.getTokenType() == TokenType.GREATER_EQUAL
                || token.getTokenType() == TokenType.LESS || token.getTokenType() == TokenType.LESS_EQUAL;
    }

    private Expression parseArithmeticExpression ()throws IOException {
        Expression expressionPart = parseMultiplyExpression();
        if(expressionPart == null) return null;
        ArithmeticExpression expr = new ArithmeticExpression();
        expr.add(expressionPart);
        while(tokenHasType(TokenType.PLUS) || tokenHasType(TokenType.MINUS)){
            Operator op = OperatorFactory.getAdditiveOperator(token.getTokenType());
            token = scanner.getToken();
            expressionPart  = parseMultiplyExpression();
            if (expressionPart == null){
                throw new ParserException("Expected expression", token.getPosition());
            }
            expr.add(expressionPart, op);
        }
        if(expr.size()==1){
            return expressionPart;
        }
        return expr;
    }

    private Expression parseMultiplyExpression ()throws IOException {
        Expression expressionPart = parsePowerExpression();
        if(expressionPart == null) return null;
        MultiplyExpression expr = new MultiplyExpression();
        expr.add(expressionPart);
        while(tokenHasType(TokenType.MULTIPLY) || tokenHasType(TokenType.DIVIDE)){
            Operator op = OperatorFactory.getOperator(token);
            token = scanner.getToken();
            expressionPart  = parsePowerExpression();
            if (expressionPart == null){
                throw new ParserException("Expected expression", token.getPosition());
            }
            expr.add(expressionPart, op);
        }
        if(expr.size()==1){
            return expressionPart;
        }
        return expr;
    }

    private Expression parsePowerExpression () throws IOException {
        Expression expressionPart = parseUnaryExpression();
        PowerExpression expr = new PowerExpression();
        expr.add(expressionPart);


        token = scanner.getToken();

        while(tokenHasType(TokenType.POWER)){
            token = scanner.getToken();
            expressionPart = parseUnaryExpression();
            expr.add(expressionPart);
            token = scanner.getToken();
        }
        if(expr.size()==1){
            return expressionPart;
        }
        return expr;
    }

    private Expression parseUnaryExpression () throws IOException {
        if(tokenHasType(TokenType.MINUS) || tokenHasType(TokenType.NOT)){
            Operator op = OperatorFactory.getOperator(token);
            token = scanner.getToken();
            UnaryExpression expr = new UnaryExpression();
            Expression expressionPart = parseExpression();
            if(expressionPart == null){
                throw new ParserException("Expected expression", token.getPosition());
            }
            expr.add(expressionPart, op);
            return expr;
        }
        Expression expressionPart = parseExpression();
        if (expressionPart == null) {
            throw new ParserException("Expected expression", token.getPosition());
        }
        return expressionPart;
    }

    private Expression parseExpression () throws IOException {
        if(tokenHasType(TokenType.OPEN_BRACKET)){
            token = scanner.getToken();
            Expression expr = parseOrExpression();
            if(expr == null) throw new ParserException("Expected expression", token.getPosition());
            if(tokenHasType(TokenType.CLOSE_BRACKET)) return expr;
            throw new ParserException(TokenType.CLOSE_BRACKET, token);
        }
        Expression value;
        if((value = parseLiteral()) != null) return value;
        if((value = parseFunctionCall()) != null) return value;
        if((value = parseVariableValue()) != null) return value;
        return null;
    }

    private FunctionCall parseFunctionCall() throws IOException {
        UnitType type = TypeManager.getUnitType(token);
        String identifier;
        if(type != null ){
            identifier = type.getName();
        }else if (tokenHasType(TokenType.IDENTIFIER)){
            identifier = token.getStringValue();
        }else{ return null; }
        if(scanner.peek().getTokenType() == TokenType.OPEN_BRACKET){
            token = scanner.getToken();
            Arguments args = parseArguments();
            return new FunctionCall(identifier, args);
        }else{
            return null;
        }
    }

    private Arguments parseArguments () throws IOException {
        return new Arguments();
    }

    private boolean tokenHasType(TokenType type) {
        return token.getTokenType() == type;
    }
}
