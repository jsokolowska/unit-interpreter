package util.tree.expression.operator;

import util.Token;

import java.util.HashMap;
import java.util.Map;

public abstract class OperatorFactory {
    private static Map<Token.TokenType, Operator> tokenToOperator = new HashMap<>();
    static {
        initTokenToOperatorMap();
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
        tokenToOperator.put(Token.TokenType.PLUS, new PlusOperator());
        tokenToOperator.put(Token.TokenType.POWER, new PowerOperator());
    }

    private static Operator createOperator (Token t){
        return tokenToOperator.get(t.getTokenType());
    }

}
