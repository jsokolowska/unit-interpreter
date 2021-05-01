package exception;

import util.Token;
import util.position.Position;

public class ParserException extends RuntimeException{

    public ParserException (Token.TokenType expected, Token.TokenType found, Position tokenPosition){
        super("ParserException in: " + tokenPosition + ": expected " + expected.toString()
                + " found " + found.toString());
    }

    public ParserException (String expected, Token.TokenType found, Position tokenPosition){
        super("ParserException in: " + tokenPosition + ": expected " + expected
                + " found " + found.toString());
    }

    public ParserException (String message, Position tokenPosition){
        super("ParserException in: " + tokenPosition + " : " + message);
    }
}
