package scanner

import exception.ScannerException
import source.StringSource
import spock.lang.*
import util.Token
import util.Token.TokenType
import util.position.Position
import util.position.PositionWrapper

class ScannerSpec extends Specification{
    def "Should recognize EOF token"(){
        given:
        def source = new PositionWrapper(new StringSource("") )
        def scanner = new Scanner(source)

        when:
        def token = scanner.getToken()

        then:
        token.getTokenType() == TokenType.EOT
        token.getPosition() == new Position(1,1)
    }

    def "Should recognize single operator tokens with unique prefixes"(){
        given:
        def source = new PositionWrapper(new StringSource(str))
        def scanner = new Scanner(source)

        expect:
        scanner.getToken() == new Token(type, new Position(1,1))

        where:
        str || type
        "(" || TokenType.OPEN_BRACKET
        ")" || TokenType.CLOSE_BRACKET
        "{" || TokenType.CURLY_OPEN
        "}" || TokenType.CURLY_CLOSE
        "," || TokenType.COMMA
        ";" || TokenType.SEMICOLON
        "+" || TokenType.PLUS
        "-" || TokenType.MINUS
        "/" || TokenType.DIVIDE
        "^" || TokenType.POWER
        "*" || TokenType.MULTIPLY

    }

    def "Should recognize double operators"(){
        given:
        def scanner = new Scanner(new StringSource(str))

        expect:
        scanner.getToken() == new Token(type, new Position(1, 1))

        where:
        str || type
        "=" || TokenType.ASSIGN
        "=="|| TokenType.EQUAL
        "!="|| TokenType.NOT_EQUAL
        "!" || TokenType.NOT
        "&&"|| TokenType.AND
        "||"|| TokenType.OR
        "<" || TokenType.LESS
        ">" || TokenType.GREATER
        "<="|| TokenType.LESS_EQUAL
        ">="|| TokenType.GREATER_EQUAL
    }

    def "Should recognize strings with double quotes"(){
        given:
        def str = "This is a string literal "
        def scanner = new Scanner(new StringSource("\"" + str + "\""))
        def token

        when:
        token = scanner.getToken()

        then:
        token.getTokenType() == TokenType.STRING_LITERAL
        token.getStringValue() ==  str
        token.getPosition() == new Position(1,1)

    }

    def "Should build integer tokens"(){
        given:
        def scanner = new Scanner(new StringSource(str))

        when:
        def token = scanner.getToken()

        then:
        token == new Token(TokenType.INT_LITERAL, value, new Position(1, 1))
        token.getIntegerValue() == value

        where:
        str     || value
        "2745"  || 2745
        "0"     || 0
    }
    def "Should build floating point tokens"(){
        given:
        def epsilon = 0.0001
        def scanner = new Scanner(new StringSource(str))

        when:
        def token = scanner.getToken()

        then:
        token == new Token(TokenType.FLOAT_LITERAL, value, new Position(1, 1))
        Math.abs(token.getDoubleValue() - (Double)value) < epsilon

        where:
        str     || value
        "9.02"  || 9.02
        "0.07"  || 0.07
        "0.0080"|| 0.008
    }

    def "Should not allow for invalid number literals"(){
        when:
        new Scanner(new StringSource(str))

        then:
        thrown(ScannerException)

        where:
        str << ["0070", "9999999999999999999999999999999999999999999999999999999999999999"]

    }

    def "If no string is provided StringSource should throw IOException"(){
        when:
        new Scanner(new StringSource(null ))

        then:
        thrown(IOException)
    }

    def "If second char of logical operator is missing Scanner should throw ScannerException"(){
        given:
        def source = new StringSource(str)

        when:
        new Scanner(source)

        then:
        def ex = thrown(ScannerException)
        ex.getMessage().contains(msg)
        ex.getMessage().contains(new Position(1,2).toString())

        where:
        str || msg
        "&-"|| "Missing &"
        "|-"|| "Missing |"
    }

    def "Should tokenize simple program"(){
        given:
        def str = "int x=3"
        def scanner = new Scanner(new StringSource(str))
        def token = scanner.getToken()

        assert token == new Token(TokenType.BASE_TYPE, new Position(1,1))
        assert token.getStringValue() == "int"
        token = scanner.getToken()
        assert token == new Token(TokenType.IDENTIFIER, new Position(1,5))
        assert token.getStringValue() == "x"
        token = scanner.getToken()
        assert token == new Token(TokenType.ASSIGN, new Position(1,6))
        token = scanner.getToken()
        assert token == new Token(TokenType.INT_LITERAL, new Position(1,7))
        assert token.getIntegerValue() == 3
        token = scanner.getToken()
        assert token == new Token(TokenType.EOT, new Position(1,8))
    }

    def "Should return valid identifier token"(){
        given:
        def scanner = new Scanner(new StringSource(str))
        def token = scanner.getToken()

        expect:
        token == new Token(TokenType.IDENTIFIER, new Position(1,1))
        token.getStringValue() == str

        where:
        str << ["x", "alaMaKota_", "ala_ma_kota", "_MaKota12", "_2"]
    }

    def "Should return unknown token"(){
        given:
        def scanner = new Scanner(new StringSource("#"))

        when:
        def token = scanner.getToken()

        then:
        token == new Token(TokenType.UNKNOWN, new Position(1,1))
    }

    def "Should throw scanner exception when given invalid identifier"(){
        when:
        new Scanner(new StringSource(str))

        then:
        thrown(ScannerException)

        where:
        str << ["__", "_#", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"]
    }

    def "Should throw scanner exception when given invalid string literal"(){
        given:
        def str = "\"a"

        when:
        new Scanner(new StringSource(str))

        then:
        thrown(ScannerException)
    }

    def "Should recognize all keywords" (){
        given:
        def scanner = new Scanner(new StringSource(str))

        expect:
        scanner.getToken() == new Token(type, new Position(1,1))

        where:
        str         || type
        "return"    || TokenType.RETURN
        "if"        || TokenType.IF
        "else"      || TokenType.ELSE
        "while"     || TokenType.WHILE
        "break"     || TokenType.BREAK
        "continue"  || TokenType.CONTINUE
        "print"     || TokenType.PRINT
        "explain"   || TokenType.EXPLAIN
        "type"      || TokenType.TYPE
        "as"        || TokenType.AS
        "unit"      || TokenType.UNIT
        "let"       || TokenType.LET
        "int"       || TokenType.BASE_TYPE
        "float"     || TokenType.BASE_TYPE
        "bool"      || TokenType.BASE_TYPE
        "string"    || TokenType.BASE_TYPE
        "compound"  || TokenType.COMPOUND
        "kilogram"  || TokenType.BASE_UNIT
        "meter"     || TokenType.BASE_UNIT
        "second"    || TokenType.BASE_UNIT
        "true"      || TokenType.BOOL_LITERAL
        "false"     || TokenType.BOOL_LITERAL
    }
}