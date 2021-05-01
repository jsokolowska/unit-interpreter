package util;

import util.position.Position;

import java.math.BigDecimal;

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
        EXPLAIN,            // explain
        TYPE,               // type
        UNIT,               // unit
        AS,                 // as
        LET,                // let

        //literals
        IDENTIFIER,         // identifier : ((underscore  (letter | digit)) | letter) {letter | digit | underscore};
        BASE_TYPE,          // int, float, bool, string
        BASE_UNIT,          // kg, meter, second
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

    public Token (TokenType type, Object value){
        this(type, value, null);
    }

    public Token (TokenType type, Position position){
        this(type, null, position);
    }

    public Token (TokenType type, Object value, Position position){
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public String getStringValue(){
        return (String) value;
    }

    public double getDoubleValue(){return (Double) value;}

    public int getIntegerValue(){return (Integer) value;}

    public TokenType getTokenType() {return type;}

    public Position getPosition() {return position;}

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
