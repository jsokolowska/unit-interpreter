package tree.expression.operator;

import tree.expression.operator.unit.*;
import util.Token;

import java.util.HashMap;
import java.util.Map;

public class OperatorFactory {
    private static final Map<Token.TokenType, Operator> tokenToOperator = new HashMap<>();
    private static final Map<Token.TokenType, UnitOperator> tokenToUnitOperator = new HashMap<>();
    static {
        initTokenToOperatorMap();
        initTokenToUnitOperatorMap();
    }
    private static void initTokenToOperatorMap(){
        tokenToOperator.put(Token.TokenType.AND, new AndOperator());
        tokenToOperator.put(Token.TokenType.DIVIDE, new DivOperator());
        tokenToOperator.put(Token.TokenType.EQUAL, new EqOperator());
        tokenToOperator.put(Token.TokenType.GREATER_EQUAL, new GreaterEqOperator());
        tokenToOperator.put(Token.TokenType.GREATER, new GreaterOperator());
        tokenToOperator.put(Token.TokenType.LESS_EQUAL, new LessEqOperator());
        tokenToOperator.put(Token.TokenType.LESS, new LessOperator());
        tokenToOperator.put(Token.TokenType.MULTIPLY, new MulOperator());
        tokenToOperator.put(Token.TokenType.NOT, new NotOperator());
        tokenToOperator.put(Token.TokenType.NOT_EQUAL, new NotEqOperator());
        tokenToOperator.put(Token.TokenType.OR, new OrOperator());
        tokenToOperator.put(Token.TokenType.MINUS, new NegOperator());
        tokenToOperator.put(Token.TokenType.POWER, new PowerOperator());
    }

    private static void initTokenToUnitOperatorMap(){
        tokenToUnitOperator.put(Token.TokenType.MINUS, new UnitNegOperator());
        tokenToUnitOperator.put(Token.TokenType.MULTIPLY, new UnitMulOperator());
        tokenToUnitOperator.put(Token.TokenType.DIVIDE, new UnitDivOperator());
        tokenToUnitOperator.put(Token.TokenType.POWER, new UnitPowerOperator());
    }

    public static Operator getOperator(Token t){
        return tokenToOperator.get(t.getTokenType());
    }

    public static Operator getAdditiveOperator(Token.TokenType t){
        if (t == Token.TokenType.PLUS) return new PlusOperator();
        if( t == Token.TokenType.MINUS) return new MinusOperator();
        return null;
    }

    public static UnitOperator getUnitOperator(Token t){
        return tokenToUnitOperator.get(t.getTokenType());
    }

    public static UnitOperator getAdditiveUnitOperator(Token t){
        if (t.getTokenType() == Token.TokenType.PLUS) return new UnitPlusOperator();
        if( t.getTokenType() == Token.TokenType.MINUS) return new UnitMinusOperator();
        return null;
    }

}
