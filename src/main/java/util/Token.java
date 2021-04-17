package util;

import util.position.Position;

public class Token {
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

        //types
        INT,                // int
        FLOAT,              // float
        BOOL,               // bool
        STRING,             // string
        COMPOUND,           // compound
        KILO,               // kilo
        METER,              // meter
        SECOND,             // second


        //literals
        IDENTIFIER,         // identifier : ((underscore  (letter | digit)) | letter) {letter | digit | underscore};
        BASE_TYPE,          // int, float, bool, string, compound, kilo, meter, second
        NUMERIC_LITERAL,    // number : "0" | non_zero_number;
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
    public Integer getIntegerValue(){return (Integer) value;}
    public Float getFloatValue(){return (Float) value;}
    public TokenType getTokenType() {return type;}

    @Override
    public String toString() {
        String str = "Token { type = " + type;
        if (value!=null){
            str += ", value = " + value;
        }
        if (position != null){
            str += ", position = " + position;
        }
        str += " }";
        return str;
    }
}
