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

    private void nextToken() throws IOException, ScannerException {
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
        }else if(buildIdentifier()){
            return;
        }
        currentToken = new Token(Token.TokenType.UNKNOWN, (char) currChar, tokenPosition);

    }

    private void ignoreWhitespaces() throws IOException {
        while (Character.isWhitespace(currChar)){
            currChar = source.get();
        }
    }

    private boolean buildEOT() throws IOException {
        if(currChar == Source.EOT){
            currentToken = new Token(Token.TokenType.EOT, tokenPosition);
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
        currentToken = new Token(Token.TokenType.INT_LITERAL, value, tokenPosition);

        if (currChar == '.'){
            currChar = source.get();
            buildFraction(value);
        }
        return true;
    }

    private int buildInteger() throws IOException {
        if(isZeroNumber()){
            return 0;
        }
        return buildNonZeroNumber();
    }

    private boolean isZeroNumber() throws IOException {
        if (currChar != '0'){
            return false;
        }
        currChar = source.get();
        if (Character.isDigit(currChar)){
            throw  new ScannerException(source.getPosition(), "Non-zero number should not start with 0");
        }
        return true;
    }

    private int buildNonZeroNumber() throws IOException {
        int value = 0;
        while(Character.isDigit(currChar) && value < Token.MAX_NUMBER){
            value = 10 * value + (currChar - '0');
            currChar = source.get();
        }
        if( Character.isDigit(currChar)){
            throw new ScannerException(tokenPosition, "Number exceeds max integer value " + Token.MAX_NUMBER);
        }
        return value;
    }

    private void buildFraction (int value) throws IOException {
        int scale = ignoreZeros();
        int fractionValue = 0;
        if (!Character.isDigit(currChar)) {
            throw new ScannerException(tokenPosition, "Improper floating point number");
        }
        while(Character.isDigit(currChar)){
            fractionValue = fractionValue * 10 + currChar - '0';
            currChar = source.get();
            scale ++;
        }
        double final_val = value + (float)fractionValue / Math.pow(10, scale);
        currentToken = new Token(Token.TokenType.FLOAT_LITERAL, final_val, tokenPosition);
    }

    private int ignoreZeros() throws IOException {
        int numIgnored = 0;
        while(currChar == '0'){
            numIgnored ++;
            currChar = source.get();
        }
        return numIgnored;
    }

    private boolean buildIdentifier() throws IOException {
        IdentifierScanner scanner = new IdentifierScanner();
        return scanner.buildIdentifier();
    }

    private class IdentifierScanner{
        private StringBuilder identifier = new StringBuilder();
        private int idLen = 0;

        public boolean buildIdentifier() throws IOException{
            if(isValidIdBeginning()){
                while(isValidIdPart() && idLen < Token.MAX_IDENTIFIER_LEN){
                    identifier.append((char)currChar);
                    idLen ++;
                    currChar = source.get();
                }
                if(isValidIdPart()){
                    //max len exceeded
                    throw new ScannerException(tokenPosition, "Invalid identifier (max identifier " +
                            "length" + Token.MAX_IDENTIFIER_LEN + ")");
                }
                if(buildKeyword(identifier)){
                    return true;
                }
                currentToken = new Token(Token.TokenType.IDENTIFIER, identifier.toString(), tokenPosition);
                return true;
            }
            return false;
        }

        private boolean isValidIdBeginning() throws IOException {
            if(Character.isAlphabetic(currChar)){
                identifier.append((char)currChar);
                currChar = source.get();
                idLen++;
                return true;
            }else if (currChar == '_'){
                identifier.append((char)currChar);
                currChar = source.get();
                if(Character.isLetterOrDigit(currChar)) {
                    identifier.append((char)currChar);
                    currChar = source.get();
                    idLen += 2;
                    return true;
                }
                // only identifiers can start with "_" so throw exception
                throw new ScannerException(source.getPosition(),"Invalid identifier");
            }
            return false;
        }

        private boolean isValidIdPart (){
            return Character.isLetterOrDigit(currChar) || currChar == '_';
        }

        private boolean buildKeyword (StringBuilder identifier){
            Token.TokenType tempType = ScannerMaps.keywords.get(identifier.toString());
            if(tempType!=null){
                currentToken = new Token(tempType, identifier.toString(), tokenPosition);
                return true;
            }
            return false;
        }
    }

}
