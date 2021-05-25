package parser;

import scanner.Scanner;
import tree.Program;
import tree.expression.Expression;
import tree.expression.math.*;
import tree.expression.operator.Operator;
import tree.expression.operator.OperatorFactory;
import tree.expression.operator.UnitOperator;
import tree.expression.unit.*;
import tree.expression.unit.value.UnitExpressionLiteral;
import tree.expression.unit.value.UnitExpressionVariableValue;
import tree.function.Arguments;
import tree.function.Function;
import tree.function.Parameters;
import tree.statement.*;
import tree.type.Type;
import tree.type.TypeManager;
import tree.type.UnitType;
import tree.unit.*;
import tree.value.FunctionCall;
import tree.value.Literal;
import tree.value.VariableValue;
import util.Token;
import util.Token.TokenType;
import util.exception.ParserException;

import java.io.IOException;

public class Parser {
    private final Scanner scanner;
    private Token token;
    private final TypeManager typeManager;

    public Parser(Scanner scanner, TypeManager typeManager) throws IOException {
        this.scanner = scanner;
        nextToken();
        this.typeManager = typeManager;
    }

    private static boolean matchesType(Token token) {
        return token.isBaseType() || token.isBaseUnit() || token.getTokenType() == TokenType.IDENTIFIER;
    }

    private static boolean matchesUnitType(Token token) {
        return token.getTokenType() == TokenType.IDENTIFIER || token.isBaseUnit();
    }

    public Program parse() throws IOException {
        Program program = new Program();

        while (true) {
            UnitDeclaration unitDeclaration;
            ConversionFunction conversionFunction;
            if ((unitDeclaration = parseUnitDeclaration())!= null) {
                program.add(unitDeclaration);
                nextToken();
            } else if ((conversionFunction = parseUnitConversion()) != null) {
                program.add(conversionFunction);
                nextToken();
            }else{
                break;
            }
        }

        Function function = parseFunction();
        // try parsing functions until you get to the end of file
        while (function != null) {
            if(program.functionExists(function.getIdentifier())){
                throw new ParserException("Function redefinition not allowed", token.getPosition());
            }
            program.add(function);
            function = parseFunction();
        }

        if (!program.hasFunctions()) {
            throw new ParserException("Program needs to have at least one function");
        }
        if(!tokenHasType(TokenType.EOT)){
            throw new ParserException("Unexpected EOT");
        }

        return program;
    }
    private void nextToken() throws IOException {
        token = scanner.getToken();
    }

    private UnitDeclaration parseUnitDeclaration() throws IOException {
        if (!tokenHasType(TokenType.UNIT)) return null;

        nextToken();
        if (token.getTokenType() == TokenType.IDENTIFIER) {
            String unitName = token.getStringValue();
            if (typeManager.exists(unitName)) {
                throw new ParserException("Unit redefinition not allowed", token.getPosition());
            }

            nextToken();
            CompoundExpr typeExpr = null;

            if (token.getTokenType() == TokenType.AS) {
                nextToken();
                typeExpr = parseCompoundExpression();
                if (typeExpr == null) {
                    throw new ParserException(TokenType.LESS, token);
                }
                nextToken();
            }
            if(typeExpr == null){
                typeManager.addUnit(unitName);
            }else{
                typeManager.addUnit(unitName, typeExpr);
            }

            if (token.getTokenType() == TokenType.SEMICOLON) {
                return new UnitDeclaration(unitName, typeExpr);
            } else {
                throw new ParserException(TokenType.SEMICOLON, token);
            }
        }
        throw new ParserException(TokenType.IDENTIFIER, token);
    }

    private CompoundExpr parseCompoundExpression() throws IOException {
        if (token.getTokenType() != TokenType.LESS) return null;

        nextToken();
        if(tokenHasType(TokenType.GREATER)) {
            throw new ParserException("Conversion Expression must contain at least one term", token.getPosition());
        }
        CompoundExpr compoundExpr = new CompoundExpr();
        parseCompoundNumerator(compoundExpr); //never returns null


        if (token.getTokenType() == TokenType.DIVIDE) {
            nextToken();
            parseCompoundDenominator(compoundExpr);
        }
        //check if compound expression has a proper ending
        if (token.getTokenType() != TokenType.GREATER) {
            throw new ParserException(TokenType.GREATER, token);
        }
        compoundExpr.simplify();
        if(compoundExpr.size()==0){
            throw new ParserException("Compound expression equivalent to empty", token.getPosition());
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
        // will always make a term or throw ParserException
        CompoundTerm one = parseOneCompoundTerm();
        if (isDenominator) one.negate();
        expr.addPart(one);
        nextToken();

        while (tokenHasType(TokenType.MULTIPLY)) {
            nextToken();
            CompoundTerm term = parseOneCompoundTerm();
            if (isDenominator) term.negate();
            expr.addPart(term);
            nextToken();
        }
    }

    private CompoundTerm parseOneCompoundTerm() throws IOException {
        if (!matchesUnitType(token)) {
            throw new ParserException("unit type", token);
        }
        UnitType unit = typeManager.getUnitType(token);
        if(unit == null) throw new ParserException("Unit usage before definition", token.getPosition());

        nextToken();
        if (!tokenHasType(TokenType.POWER)) return new CompoundTerm(unit, 1);

        nextToken();
        int exponent;
        if(tokenHasType(TokenType.MINUS)){
            nextToken();
            if (!tokenHasType(TokenType.INT_LITERAL)) throw new ParserException(TokenType.INT_LITERAL, token);
            exponent = token.getIntegerValue() * -1;
        }else{
            if (!tokenHasType(TokenType.INT_LITERAL)) throw new ParserException(TokenType.INT_LITERAL, token);
            exponent = token.getIntegerValue();
        }
        if (exponent == 0) throw new ParserException("exponent in unit expression cannot be 0", token.getPosition());

        return new CompoundTerm(unit, exponent);
    }

    private ConversionFunction parseUnitConversion() throws IOException {
        if(!tokenHasType(TokenType.LET)) return null;

        nextToken();
        if(! matchesUnitType(token)){
            throw new ParserException("Expected unit type", token);
        }
        UnitType unit = typeManager.getUnitType(token);
        if(unit == null) {
            throw new ParserException("Unit usage before definition", token.getPosition());
        }
        nextToken();
        if(!tokenHasType(TokenType.AS)){
            throw new ParserException(TokenType.AS, token);
        }
        nextToken();

        UnitParameters parameters = parseUnitParameters();
        if (parameters == null){
            throw new ParserException(TokenType.OPEN_BRACKET, token);
        }else{
            nextToken();
            if (!tokenHasType(TokenType.CURLY_OPEN)){
                throw new ParserException(TokenType.CURLY_OPEN, token);
            }
            nextToken();
            UnitExpression conversionExpression = parseConversionExpression();


            if(! tokenHasType(TokenType.CURLY_CLOSE)){
                throw new ParserException(TokenType.CURLY_CLOSE, token);
            }
            nextToken();
            if(!tokenHasType(TokenType.SEMICOLON)){
                throw new ParserException(TokenType.SEMICOLON, token);
            }
            return new ConversionFunction(unit, parameters, conversionExpression);
        }
    }

    //todo
    private UnitParameters parseUnitParameters () throws IOException{
        if(!tokenHasType(TokenType.OPEN_BRACKET)) return null;

        nextToken();
        UnitParameters params = new UnitParameters();
        //if after open bracket there is immediately close bracket parameter list is empty
        if(tokenHasType(TokenType.CLOSE_BRACKET)) return params;

        parseUnitParameter(params);
        nextToken();
        while(tokenHasType(TokenType.COMMA)){
            nextToken();
            parseUnitParameter(params);
            nextToken();
        }
        if(!tokenHasType(TokenType.CLOSE_BRACKET)){
            throw new ParserException(TokenType.CLOSE_BRACKET, token);
        }
        return params;
    }

    //todo
    private void parseUnitParameter(UnitParameters parameters) throws IOException {
        if (!matchesUnitType(token)) {
            throw new ParserException("Expected type", token);
        }
        UnitType type = typeManager.getUnitType(token);
        if (type == null) {
            throw new ParserException("Type usage before definition", token.getPosition());
        }
        nextToken();
        if (!tokenHasType(TokenType.IDENTIFIER)){
            throw new ParserException(TokenType.IDENTIFIER, token);
        }
        parameters.addParameter(token.getStringValue(), type);
    }

    private Parameters parseParameters () throws IOException {
        if(!tokenHasType(TokenType.OPEN_BRACKET)) return null;

        nextToken();
        Parameters params = new Parameters();
        //if after open bracket there is immediately close bracket parameter list is empty
        if(tokenHasType(TokenType.CLOSE_BRACKET)) return params;

        parseParameter(params);
        nextToken();
        while(tokenHasType(TokenType.COMMA)){
            nextToken();
            parseParameter(params);
            nextToken();

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
        nextToken();
        if (!tokenHasType(TokenType.IDENTIFIER)){
            throw new ParserException(TokenType.IDENTIFIER, token);
        }
        parameters.addParameter(token.getStringValue(), type);
    }

    private UnitExpression parseConversionExpression() throws IOException {
        UnitExpression expressionPart = parseMulUnitExpression();
        ConversionExpression expression = new ConversionExpression();
        expression.add(expressionPart);
        while(tokenHasType(TokenType.PLUS) || tokenHasType(TokenType.MINUS) ){
            UnitOperator op = (UnitOperator) OperatorFactory.getAdditiveOperator(token.getTokenType());
            nextToken();
            expressionPart = parseMulUnitExpression();
            expression.add(expressionPart, op);
        }
        if(expression.size()>1){
            return expression;
        }
        return expressionPart;
    }

    private UnitExpression parseMulUnitExpression () throws IOException {
        UnitExpression expressionPart = parsePowerUnitExpression();
        MulUnitExpression expression = new MulUnitExpression();
        expression.add(expressionPart);

        while(tokenHasType(TokenType.DIVIDE) || tokenHasType(TokenType.MULTIPLY) ){
            UnitOperator op = (UnitOperator) OperatorFactory.getOperator(token);
            nextToken();
            expressionPart = parsePowerUnitExpression();
            expression.add(expressionPart, op);
        }
        if(expression.size()>1){
            return expression;
        }
        return expressionPart;
    }

    private UnitExpression parsePowerUnitExpression () throws IOException {
        UnitExpression expressionPart = parseUnaryUnitExpression();
        PowerUnitExpression expression = new PowerUnitExpression();
        expression.add(expressionPart);

        nextToken();
        while(tokenHasType(TokenType.POWER) ){
            nextToken();
            expressionPart = parseUnaryUnitExpression();
            expression.add(expressionPart);
            nextToken();
        }
        if(expression.size()>1){
            return expression;
        }
        return expressionPart;
    }

    private UnitExpression parseUnaryUnitExpression () throws IOException {
        if(tokenHasType(TokenType.MINUS)){
            nextToken();
            UnaryUnitExpression expr = new UnaryUnitExpression();
            expr.add(parseUnitExpression());
            return expr;
        }
        return parseUnitExpression();
    }

    private UnitExpression parseUnitExpression () throws IOException {
        if(tokenHasType(TokenType.OPEN_BRACKET)){
            nextToken();
            if(tokenHasType(TokenType.CLOSE_BRACKET)){
                throw new ParserException("Empty parenthesis, expected expression", token.getPosition());
            }
            UnitExpression expr = parseConversionExpression();
            if(!tokenHasType(TokenType.CLOSE_BRACKET)){
                throw new ParserException(TokenType.CLOSE_BRACKET, token);
            }
            return expr;
        }
        UnitExpression value;
        if((value = parseUnitNumberLiteral()) != null) return value;
        if((value = parseUnitVariableValue()) != null) return value;
        throw new ParserException("number literal or variable", token);
    }

    private UnitExpressionLiteral<?> parseUnitNumberLiteral (){
        return switch (token.getTokenType()){
            case INT_LITERAL -> new UnitExpressionLiteral<>(token.getIntegerValue());
            case FLOAT_LITERAL -> new UnitExpressionLiteral<>(token.getDoubleValue());
            default -> null;
        };
    }

    private UnitExpressionVariableValue parseUnitVariableValue (){
        if(!tokenHasType(TokenType.IDENTIFIER)) return null;
        return new UnitExpressionVariableValue(token.getStringValue());
    }

    private VariableValue parseVariableValue (){
        if(!tokenHasType(TokenType.IDENTIFIER)) return null;
        return new VariableValue(token.getStringValue());
    }

    private Literal<?> parseLiteral (){
        return switch (token.getTokenType()){
            case STRING_LITERAL -> new Literal<>(token.getStringValue());
            case BOOL_LITERAL -> new Literal<>(token.getBoolValue());
            case INT_LITERAL -> new Literal<>(token.getIntegerValue());
            case FLOAT_LITERAL -> new Literal<>(token.getDoubleValue());
            default -> null;
        };
    }

    private Function parseFunction() throws IOException {
        Type type = typeManager.getUnitType(token);
        if(type == null){
            if((type = typeManager.getType(token))==null){
                if(tokenHasType(TokenType.IDENTIFIER)){
                    throw new ParserException("Type usage before definition", token.getPosition());
                }else{
                    return null;
                }
            }
        }
        nextToken();
        if(!tokenHasType(TokenType.IDENTIFIER)){
            throw new ParserException(TokenType.IDENTIFIER, token);
        }
        String identifier = token.getStringValue();
        nextToken();
        Parameters params = parseParameters();
        nextToken();
        BlockStatement statement = parseBlockStatement();
        if(statement == null) throw new ParserException(TokenType.CURLY_OPEN, token);

        return new Function(identifier, statement, params, type);
    }

    protected Statement parseStatement() throws IOException {
        int line = token.getLine();
        Statement st ;
        if ((st = parseBlockStatement() )!= null) return addLine(st, line);
        if ((st = parseReturn() )!= null) return addLine(st, line);
        if ((st = parseBreak())!= null) return addLine(st, line);
        if ((st = parseContinue())!= null) return addLine(st, line);
        if ((st = parseLoop() )!= null) return addLine(st, line);
        if ((st = parseIfElseStatement() )!= null) return addLine(st, line);
        if ((st = parsePrintStatement())!= null) return addLine(st, line);
        if ((st = parseExplainStatement() )!= null) return addLine(st, line);
        if ((st = parseTypeStatement() )!= null) return addLine(st, line);
        /* these functions share common beginning*/
        if ((st = parseAssignStatement() )!= null) return addLine(st, line);
        if ((st = parseVariableDeclarationStatement() )!= null) return addLine(st, line);
        if ((st = parseCallStatement() )!= null) return addLine(st, line);
        return null;
    }

    private Statement addLine(Statement st, int line){
        st.setLine(line);
        return st;
    }

    private IfElseStatement parseIfElseStatement () throws IOException {
        if(!tokenHasType(TokenType.IF)) return null;
        nextToken();
        if(!tokenHasType(TokenType.OPEN_BRACKET)){
            throw new ParserException(TokenType.OPEN_BRACKET, token);
        }
        nextToken();
        Expression ifCondition = parseExpression();
        nextToken();
        if(!tokenHasType(TokenType.CLOSE_BRACKET)){
            throw new ParserException(TokenType.CLOSE_BRACKET, token);
        }
        nextToken();
        Statement ifBody = parseStatement();
        if(ifBody == null){
            throw new ParserException("Empty if body", token.getPosition());
        }
        if(tokenHasType(TokenType.ELSE)){
            nextToken();
            Statement elseBody = parseStatement();
            if(elseBody == null){
                throw new ParserException("Empty else body", token.getPosition());
            }

            return new IfElseStatement(ifCondition, ifBody, elseBody);
        }
        return new IfElseStatement(ifCondition, ifBody);
    }

    private CallStatement parseCallStatement () throws IOException {
        FunctionCall funCall = parseFunctionCall();
        if(funCall == null) return null;
        nextToken();
        if(!tokenHasType(TokenType.SEMICOLON)){
            throw new ParserException(TokenType.SEMICOLON, token);
        }
        nextToken();
        return new CallStatement(funCall);
    }

    private PrintStatement parsePrintStatement () throws IOException {
        if(!tokenHasType(TokenType.PRINT)) return null;
        nextToken();
        Arguments args = parseArguments();
        nextToken();
        if(!tokenHasType(TokenType.SEMICOLON)){
            throw new ParserException(TokenType.SEMICOLON, token);
        }
        nextToken();
        return new PrintStatement(args);
    }

    private ExplainStatement parseExplainStatement () throws IOException {
        if(!tokenHasType(TokenType.EXPLAIN)) return null;
        nextToken();
        if(!tokenHasType(TokenType.OPEN_BRACKET)){
            throw new ParserException(TokenType.OPEN_BRACKET, token);
        }
        nextToken();
        UnitType type = typeManager.getUnitType(token);
        if(type == null) {
            throw  new ParserException("Expected unit type", token);
        }
        nextToken();
        if(!tokenHasType(TokenType.CLOSE_BRACKET)){
            throw new ParserException(TokenType.CLOSE_BRACKET, token);
        }
        nextToken();
        if(!tokenHasType(TokenType.SEMICOLON)){
            throw new ParserException(TokenType.SEMICOLON, token);
        }
        nextToken();
        return new ExplainStatement(type);
    }

    //must be called before variable declaration parsing
    private AssignStatement parseAssignStatement () throws IOException {
        if(!tokenHasType(TokenType.IDENTIFIER)) return null;
        if(!requireNextTokenType(TokenType.ASSIGN)) return null;

        String identifier = token.getStringValue();
        nextToken(); //must be "=" token
        nextToken();
        Expression expression = parseOrExpression();
        if(expression == null){
            throw new ParserException("Empty assignment body", token.getPosition());
        }
        if(!tokenHasType(TokenType.SEMICOLON)) {
            throw new ParserException(TokenType.SEMICOLON, token);
        }
        nextToken();
        return new AssignStatement(identifier, expression);
    }

    private VariableDeclarationStatement parseVariableDeclarationStatement() throws IOException {
        Type type = typeManager.getType(token);
        if(type == null ) return null; //first token does not match type
        if(!requireNextTokenType(TokenType.IDENTIFIER)) return null; // can be function call
        nextToken();
        AssignStatement assign = parseAssignStatement();
        if(assign == null){
            if(!tokenHasType(TokenType.IDENTIFIER)){
                throw new ParserException(TokenType.IDENTIFIER, token);
            }
            String id = token.getStringValue();
            nextToken();
            if(!tokenHasType(TokenType.SEMICOLON)){
                throw new ParserException(TokenType.SEMICOLON, token);
            }
            nextToken();
            return new VariableDeclarationStatement(type, id, null);

        }else{
            return new VariableDeclarationStatement(type, assign.getIdentifier(), assign.getAssignExpression());
        }
    }

    private TypeStatement parseTypeStatement () throws IOException {
        if(!tokenHasType(TokenType.TYPE)) return null;
        nextToken();
        if(!tokenHasType(TokenType.OPEN_BRACKET)){
            throw new ParserException(TokenType.OPEN_BRACKET, token);
        }
        nextToken();
        if(!tokenHasType(TokenType.IDENTIFIER)){
            throw new ParserException(TokenType.IDENTIFIER, token);
        }
        String identifier = token.getStringValue();
        nextToken();
        if(!tokenHasType(TokenType.CLOSE_BRACKET)){
            throw new ParserException(TokenType.CLOSE_BRACKET, token);
        }
        nextToken();
        if(!tokenHasType(TokenType.SEMICOLON)){
            throw new ParserException(TokenType.SEMICOLON, token);
        }
        nextToken();
        return new TypeStatement(identifier);
    }

    private BlockStatement parseBlockStatement() throws IOException {
        if(!tokenHasType(TokenType.CURLY_OPEN)) return null;
        nextToken();
        BlockStatement block = new BlockStatement();
        Statement stmt;
        while(true){
            if((stmt = parseStatement()) != null){
                block.add(stmt);

            }else{
                break;
            }
        }
        if(!tokenHasType(TokenType.CURLY_CLOSE)){
            throw new ParserException(TokenType.CURLY_CLOSE, token);
        }
        nextToken();
        return block;
    }

    private WhileStatement parseLoop() throws IOException {
        if (!tokenHasType(TokenType.WHILE)) return null;
        nextToken();

        if(!tokenHasType(TokenType.OPEN_BRACKET)) {
            throw new ParserException(TokenType.OPEN_BRACKET, token);
        }
        nextToken();
        Expression cond = parseOrExpression();
        if (cond == null){
            throw  new ParserException("Expected conditional expression", token.getPosition());
        }
        if(!tokenHasType(TokenType.CLOSE_BRACKET)){
            throw new ParserException(TokenType.CLOSE_BRACKET, token);
        }
        nextToken();
        Statement body = parseStatement();
        if (body == null){
            throw new ParserException(TokenType.CURLY_OPEN, token);
        }
        return new WhileStatement(body, cond);
    }

    private ReturnStatement parseReturn() throws IOException {
        if (!tokenHasType(TokenType.RETURN)) return null;

        nextToken();
        //build value if exists
        if (tokenHasType(TokenType.SEMICOLON)) {
            nextToken();
            return new ReturnStatement(null);
        }else{
            Expression expr;
            expr = parseOrExpression();
            if(tokenHasType(TokenType.SEMICOLON)){
                nextToken();
                return new ReturnStatement(expr);
            }
        }
        throw new ParserException(TokenType.SEMICOLON, token);
    }

    private BreakStatement parseBreak() throws IOException {
        if (!tokenHasType(TokenType.BREAK)) return null;

        nextToken();
        if (tokenHasType(TokenType.SEMICOLON)) {
            nextToken();
            return new BreakStatement();
        }
        throw new ParserException(TokenType.SEMICOLON, token);
    }

    private ContinueStatement parseContinue() throws IOException {
        if (!tokenHasType(TokenType.CONTINUE)) return null;
        nextToken();
        if (tokenHasType(TokenType.SEMICOLON)) {
            nextToken();
            return new ContinueStatement();
        }
        throw new ParserException(TokenType.SEMICOLON, token);
    }

    private Expression parseOrExpression() throws IOException {
        OrExpression expression = new OrExpression();
        Expression expressionComponent = parseAndExpression();
        expression.add(expressionComponent);
        while (tokenHasType(TokenType.OR)){
            nextToken();
            expressionComponent = parseAndExpression();
            expression.add(expressionComponent);
        }
        if(expression.size() == 1)  return expressionComponent;
        return expression;
    }

    private Expression parseAndExpression() throws IOException {
        AndExpression expression = new AndExpression();
        Expression expressionComponent = parseComparisonExpression();
        expression.add(expressionComponent);

        while(tokenHasType(TokenType.AND)){
            nextToken();
            expressionComponent = parseComparisonExpression();
            expression.add(expressionComponent);
        }
        if(expression.size()==1) return expressionComponent;
        return expression;
    }

    private Expression parseComparisonExpression ()throws IOException {
        Expression expressionPart = parseRelationalExpression();
        ComparisonExpression expr = new ComparisonExpression();
        expr.add(expressionPart);

        while(tokenHasType(TokenType.EQUAL) || tokenHasType(TokenType.NOT_EQUAL)){
            Operator op = OperatorFactory.getOperator(token);
            nextToken();
            expressionPart  = parseRelationalExpression();
            expr.add(expressionPart, op);
        }
        if(expr.size()==1){
            return expressionPart;
        }
        return expr;
    }

    private Expression parseRelationalExpression ()throws IOException {
        Expression expressionPart = parseArithmeticExpression();
        RelationalExpression expr = new RelationalExpression();
        expr.add(expressionPart);

        while(isRelOp(token)){
            Operator op = OperatorFactory.getOperator(token);
            nextToken();
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
        ArithmeticExpression expr = new ArithmeticExpression();
        expr.add(expressionPart);
        while(tokenHasType(TokenType.PLUS) || tokenHasType(TokenType.MINUS)){
            Operator op = OperatorFactory.getAdditiveOperator(token.getTokenType());
            nextToken();
            expressionPart  = parseMultiplyExpression();
            expr.add(expressionPart, op);
        }
        if(expr.size()==1){
            return expressionPart;
        }
        return expr;
    }

    private Expression parseMultiplyExpression ()throws IOException {
        Expression expressionPart = parsePowerExpression();
        MultiplyExpression expr = new MultiplyExpression();
        expr.add(expressionPart);
        while(tokenHasType(TokenType.MULTIPLY) || tokenHasType(TokenType.DIVIDE)){
            Operator op = OperatorFactory.getOperator(token);
            nextToken();
            expressionPart  = parsePowerExpression();
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

        nextToken();

        while(tokenHasType(TokenType.POWER)){
            nextToken();
            expressionPart = parseUnaryExpression();
            expr.add(expressionPart);
            nextToken();
        }
        if(expr.size()==1){
            return expressionPart;
        }
        return expr;
    }

    private Expression parseUnaryExpression () throws IOException {
        if(tokenHasType(TokenType.MINUS) || tokenHasType(TokenType.NOT)){
            Operator op = OperatorFactory.getOperator(token);
            nextToken();
            UnaryExpression expr = new UnaryExpression();
            Expression expressionPart = parseExpression();
            expr.add(expressionPart, op);
            return expr;
        }
        return parseExpression();
    }

    private Expression parseExpression () throws IOException {
        if(tokenHasType(TokenType.OPEN_BRACKET)){
            nextToken();
            Expression expr = parseOrExpression();
            if(tokenHasType(TokenType.CLOSE_BRACKET)) return expr;
            throw new ParserException(TokenType.CLOSE_BRACKET, token);
        }
        Expression value;
        if((value = parseLiteral()) != null) return value;
        if((value = parseFunctionCall()) != null) return value;     // don't switch
        if((value = parseVariableValue()) != null) return value;    // those two
        throw new ParserException("Expected literal, function call or variable reference", token);
    }

    private FunctionCall parseFunctionCall() throws IOException {
        UnitType type = typeManager.getUnitType(token);
        String identifier;
        if(type != null ){
            identifier = type.getName();
        }else if (tokenHasType(TokenType.IDENTIFIER)){
            identifier = token.getStringValue();
        }else{ return null; }

        if(!requireNextTokenType(TokenType.OPEN_BRACKET)) return null; // could be variable
        nextToken();
        Arguments args = parseArguments();
        return new FunctionCall(identifier, args);
    }

    private Arguments parseArguments () throws IOException {
        Arguments args = new Arguments();
        if(!tokenHasType(TokenType.OPEN_BRACKET)){
            throw new ParserException(TokenType.OPEN_BRACKET, token);
        }
        nextToken();
        if(tokenHasType(TokenType.CLOSE_BRACKET)){
            return args;
        }
        Expression expression = parseOrExpression();
        if(expression == null) return args;
        args.addArgument(expression);
        while(tokenHasType(TokenType.COMMA)){
            nextToken();
            expression = parseOrExpression();
            if(expression == null){
                throw new ParserException("Expected argument", token.getPosition());
            }
            args.addArgument(expression);

        }
        if(!tokenHasType(TokenType.CLOSE_BRACKET)){
            throw new ParserException(TokenType.CLOSE_BRACKET, token);
        }
        return args;
    }

    private boolean tokenHasType(TokenType type) {
        return token.getTokenType() == type;
    }
    private boolean requireNextTokenType(TokenType type){
        return scanner.peek().getTokenType() == type;
    }
}
