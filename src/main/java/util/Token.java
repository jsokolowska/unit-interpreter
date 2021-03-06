package util;

import util.position.Position;

public class Token {
    public static int MAX_NUMBER = Integer.MAX_VALUE;
    public static int MAX_IDENTIFIER_LEN = 100;
    public enum TokenType{
        //operators
        EQUAL,              // ==
        NOT_EQUAL,          // !=
        GREATER,            // >
        LESS,               // <
        GREATER_EQUAL,      // >=
        LESS_EQUAL,         // <=
        ASSIGN,             // =

        NOT,                // !
        AND,                // &&
        OR,                 // ||


        PLUS,               // +
        MINUS,              // -
        MULTIPLY,           // *
        DIVIDE,             // /
        POWER,              // ^

        OPEN_BRACKET,       // (
        CLOSE_BRACKET,      // )
        CURLY_OPEN,         // {
        CURLY_CLOSE,        // }

        COMMA,              // ,
        SEMICOLON,          // ;

        //keywords
        RETURN,             // return
        IF,                 // if
        ELSE,               // else
        WHILE,              // while
        BREAK,              // break
        CONTINUE,           // continue
        PRINT,              // print
        TYPE,               // type
        UNIT,               // unit
        AS,                 // as
        LET,                // let

        //literals
        IDENTIFIER,         // identifier : ((underscore  (letter | digit)) | letter) {letter | digit | underscore};
        TYPE_INT,
        TYPE_FLOAT,
        TYPE_BOOL,
        TYPE_STRING,
        TYPE_METER,
        TYPE_KG,
        TYPE_SEC,
        TYPE_VOID,
        COMPOUND,           // compound
        INT_LITERAL,        // number : "0" | non_zero_number;
        FLOAT_LITERAL,      // for floating point values
        STRING_LITERAL,     // string: "\"" {character} "\"";
        BOOL_LITERAL,       // "true", "false"

        UNKNOWN,             // Unknown token
        EOT
    }

    private final Object value;
    private final TokenType type;
    private final Position position;

    public Token (TokenType type){
        this(type, null, null);
    }

    public Token (TokenType type, Position position){
        this(type, null, position);
    }

    public Token (TokenType type, Object value, Position position){
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public Boolean getBoolValue(){
        return (Boolean) value;
    }

    public String getStringValue(){
        return (String) value;
    }

    public double getDoubleValue(){return (Double) value;}

    public int getIntegerValue(){return (Integer) value;}

    public TokenType getTokenType() {return type;}

    public Position getPosition() {return position;}

    public int getLine(){
        return position.getLine();
    }

    public boolean isBaseUnit (){
        return type == TokenType.TYPE_METER
                || type == TokenType.TYPE_SEC
                || type == TokenType.TYPE_KG;
    }

    public boolean isBaseType (){
        return  type == TokenType.TYPE_BOOL
                || type == TokenType.TYPE_FLOAT
                || type == TokenType.TYPE_INT
                || type == TokenType.TYPE_STRING;
    }

    @Override
    public String toString() {
        String str = "Token { type = " + type;
        if (value!=null){
            str += ", value = \"" + value + "\"";
        }
        if (position != null){
            str += ", position = " + position ;
        }
        str += " }";
        return str;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return  false;
        }
        if(this.getClass()!=obj.getClass()){
            return false;
        }
        Token token = (Token) obj;
        if(this.getTokenType()!= token.getTokenType()){
            return false;
        }
        return this.getPosition().equals(token.getPosition());
    }

}
