package scanner;

import util.Token;

import java.util.HashMap;
import java.util.Map;

public class ScannerMaps {
    //maps keywords to token types
    protected static final Map<String, Token.TokenType> keywords = new HashMap<>();

    //contains one char operators that share no common prefix with other operators and their token types
    protected static final Map<Character, Token.TokenType> singleOperators = new HashMap<>();

    static{
        initKeywordMap();
        initSingleCharMap();
    }

    private static void initKeywordMap(){
        keywords.put("return", Token.TokenType.RETURN);
        keywords.put("if", Token.TokenType.IF);
        keywords.put("else", Token.TokenType.ELSE);
        keywords.put("while", Token.TokenType.WHILE);
        keywords.put("break", Token.TokenType.BREAK);
        keywords.put("continue", Token.TokenType.CONTINUE);
        keywords.put("print", Token.TokenType.PRINT);
        keywords.put("explain", Token.TokenType.EXPLAIN);
        keywords.put("type", Token.TokenType.TYPE);
        keywords.put("unit", Token.TokenType.UNIT);
        keywords.put("as", Token.TokenType.AS);
        keywords.put("let", Token.TokenType.LET);
        keywords.put("int", Token.TokenType.TYPE_INT);
        keywords.put("float", Token.TokenType.TYPE_FLOAT);
        keywords.put("bool", Token.TokenType.TYPE_BOOL);
        keywords.put("string", Token.TokenType.TYPE_STRING);
        keywords.put("compound", Token.TokenType.COMPOUND);
        keywords.put("kilogram", Token.TokenType.TYPE_KG);
        keywords.put("meter", Token.TokenType.TYPE_METER);
        keywords.put("second", Token.TokenType.TYPE_SEC);
    }

    private static void initSingleCharMap(){
        singleOperators.put('(', Token.TokenType.OPEN_BRACKET);
        singleOperators.put(')', Token.TokenType.CLOSE_BRACKET);
        singleOperators.put('{', Token.TokenType.CURLY_OPEN);
        singleOperators.put('}', Token.TokenType.CURLY_CLOSE);
        singleOperators.put(',', Token.TokenType.COMMA);
        singleOperators.put(';', Token.TokenType.SEMICOLON);
        singleOperators.put('+', Token.TokenType.PLUS);
        singleOperators.put('-', Token.TokenType.MINUS);
        singleOperators.put('/', Token.TokenType.DIVIDE);
        singleOperators.put('^', Token.TokenType.POWER);
        singleOperators.put('*', Token.TokenType.MULTIPLY);

    }
}
