package scanner;

import exception.ScannerException;
import source.Source;
import util.Token;
import util.position.Position;
import util.position.PositionWrapper;

import java.io.IOException;

public class Scanner {
    private final PositionWrapper source;
    private Token currentToken;
    private int currChar;
    private Position tokenPosition;

    public Scanner (PositionWrapper positionWrapper) throws IOException, ScannerException {
        this.source = positionWrapper;
        currentToken = new Token(Token.TokenType.UNKNOWN);
        currChar = source.get();
        nextToken();
    }
    public Scanner (Source source) throws IOException, ScannerException {
        this(new PositionWrapper(source));
    }
    public Token getToken() throws IOException, ScannerException {
        Token temp = currentToken;
        nextToken();
        return temp;
    }
    public void nextToken() throws IOException, ScannerException {
        ignoreWhitespaces();
        tokenPosition = source.getPosition();
        if (buildEOT()){
            return;
        }else if (buildOperators()){
            return;
        }
        currentToken = new Token(Token.TokenType.UNKNOWN, currChar, tokenPosition);

    }
    private void ignoreWhitespaces() throws IOException {
        while (Character.isWhitespace(currChar)){
            currChar = source.get();
        }
    }
    private boolean buildEOT() throws IOException {
        if(currChar == Source.EOT){
            currentToken = new Token(Token.TokenType.EOT);
            currChar = source.get();
            return true;
        }
        return false;
    }
    private boolean buildOperators() throws IOException, ScannerException {
        Token.TokenType tempType = ScannerMaps.singleOperators.get((char)currChar);
        if (tempType != null)
        {
            currentToken = new Token(tempType, currChar, tokenPosition);
            currChar = source.get();
            return true;
        }
        return buildDoubleOperators();
    }
    private boolean buildDoubleOperators() throws IOException, ScannerException {
        int firstChar = currChar;
        currChar = source.get();
        switch(firstChar){
            case '=':
                if (currChar == '='){
                    currentToken = new Token(Token.TokenType.EQUAL, "==", tokenPosition);
                    currChar = source.get();
                }else{
                    currentToken = new Token(Token.TokenType.ASSIGN, "=", tokenPosition);
                }break;
            case '!':
                if(currChar == '='){
                    currentToken = new Token(Token.TokenType.NOT_EQUAL, "!=", tokenPosition);
                    currChar = source.get();
                }else{
                    currentToken = new Token(Token.TokenType.NOT, "!", tokenPosition);
                }break;
            case '<':
                if(currChar == '='){
                    currentToken = new Token(Token.TokenType.LESS_EQUAL, "<=", tokenPosition);
                    currChar = source.get();
                }else{
                    currentToken = new Token(Token.TokenType.LESS, "<", tokenPosition);
                }break;
            case '>':
                if(currChar == '='){
                    currentToken = new Token(Token.TokenType.GREATER_EQUAL, ">=", tokenPosition);
                    currChar = source.get();
                }else{
                    currentToken = new Token(Token.TokenType.GREATER, ">", tokenPosition);
                }break;
            case '&':
                if (currChar =='&'){
                    currentToken = new Token(Token.TokenType.AND, "&&", tokenPosition);
                    currChar = source.get();
                }else{
                    throw new ScannerException(source.getPosition(), "Missing &");
                }break;
            case '|':
                if (currChar == '|'){
                    currentToken = new Token(Token.TokenType.OR, "||", tokenPosition);
                    currChar = source.get();
                }else{
                    throw new ScannerException(source.getPosition(), "Missing |");
                }break;
            default:
                return false;
        }
        return true;
    }
    private boolean buildIdentifier() throws IOException{
        return false;
    }
}
