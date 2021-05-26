package util.exception;

import util.Token;
import util.position.Position;

public class ParserException extends RuntimeException{

    public ParserException (Token.TokenType expected, Token token){
        super("ParserException in: " + token.getPosition() + "- expected " + expected
                + " found " + token.getTokenType());
    }

    public ParserException (String message, Token token){
        super("ParserException in: " + token.getPosition() + "- " + message
                + " found " + token.getTokenType());
    }

    public ParserException (String message, Position tokenPosition){
        super("ParserException in: " + tokenPosition + " - " + message);
    }

    public ParserException (String message){
        super("ParserException: " + message);
    }

}
