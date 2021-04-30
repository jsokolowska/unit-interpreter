package exception;

import util.Token;
import util.position.Position;

public class ParserException extends RuntimeException{
    public ParserException (Token.TokenType expected, Token.TokenType found, Position tokenPosition){
        super("ParserException in: " + tokenPosition + ": expected - " + expected.toString()
                + " found - " + found.toString());
    }
}
