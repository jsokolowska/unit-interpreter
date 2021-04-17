package scanner;

import source.Source;
import util.Token;

public class Scanner {
    private Source source;
    private Token currentToken;

    public Token getToken(){

        return new Token(Token.TokenType.UNKNOWN);
    }
    public void parseNextToken(){

    }


}
