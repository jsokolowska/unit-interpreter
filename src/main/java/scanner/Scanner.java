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
        }else if (buildStringLiteral()){
            return;
        }else if (buildNumber()){
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
        switch(firstChar){
            case '=':
                currChar = source.get();
                if (currChar == '='){
                    currentToken = new Token(Token.TokenType.EQUAL, "==", tokenPosition);
                    currChar = source.get();
                }else{
                    currentToken = new Token(Token.TokenType.ASSIGN, "=", tokenPosition);
                }break;
            case '!':
                currChar = source.get();
                if(currChar == '='){
                    currentToken = new Token(Token.TokenType.NOT_EQUAL, "!=", tokenPosition);
                    currChar = source.get();
                }else{
                    currentToken = new Token(Token.TokenType.NOT, "!", tokenPosition);
                }break;
            case '<':
                currChar = source.get();
                if(currChar == '='){
                    currentToken = new Token(Token.TokenType.LESS_EQUAL, "<=", tokenPosition);
                    currChar = source.get();
                }else{
                    currentToken = new Token(Token.TokenType.LESS, "<", tokenPosition);
                }break;
            case '>':
                currChar = source.get();
                if(currChar == '='){
                    currentToken = new Token(Token.TokenType.GREATER_EQUAL, ">=", tokenPosition);
                    currChar = source.get();
                }else{
                    currentToken = new Token(Token.TokenType.GREATER, ">", tokenPosition);
                }break;
            case '&':
                currChar = source.get();
                if (currChar =='&'){
                    currentToken = new Token(Token.TokenType.AND, "&&", tokenPosition);
                    currChar = source.get();
                }else{
                    throw new ScannerException(source.getPosition(), "Missing &");
                }break;
            case '|':
                currChar = source.get();
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
    private boolean buildStringLiteral () throws ScannerException, IOException {
        if(currChar !='"'){
            return false;
        }
        currChar = source.get();
        StringBuilder value = new StringBuilder();
        while(currChar != '"'){
            if (currChar == Source.EOT) {
                // this situation means that
                throw new ScannerException(tokenPosition, "Missing \"");
            }
            value.append((char) currChar);
            currChar = source.get();
        }
        currentToken = new Token(Token.TokenType.STRING_LITERAL, value.toString(), tokenPosition);
        currChar = source.get();
        return true;
    }
    private boolean buildNumber() throws IOException {
        if (!Character.isDigit(currChar)){
            return false;
        }
        // first character is digit so its either a number or a mistake
        int value = buildInteger();
        float fraction = 0;
        if (currChar == '.'){
            currChar = source.get();
            fraction = buildFraction();
        }
        currentToken = new Token(Token.TokenType.NUMERIC_LITERAL, value + fraction, tokenPosition);
        return true;
    }
    private int buildInteger() throws IOException {
        if(isZeroNum()){
            return 0;
        }
        return buildNonZeroNum();
    }
    private boolean isZeroNum() throws IOException {
        if (currChar != '0'){
            return false;
        }
        currChar = source.get();
        if (Character.isDigit(currChar)){
            throw  new ScannerException(tokenPosition, "Number should not start with 0");
        }
        return true;
    }
    private int buildNonZeroNum() throws IOException {
        int value = 0;
        while(Character.isDigit(currChar)){
            value = 10 * value + (currChar - '0');
            currChar = source.get();
        }
        return value;
    }
    private float buildFraction () throws IOException {
        int exponent = ignoreZeros() + 1;
        float value = 0;
        while(Character.isDigit(currChar)){
            value += (currChar - '0')/(float)(Math.pow(10, exponent));
            currChar = source.get();
            exponent ++;
        }
        return value;
    }
    private int ignoreZeros() throws IOException {
        int numIgnored = 0;
        while(currChar == '0'){
            numIgnored ++;
            currChar = source.get();
        }
        return numIgnored;
    }
}
