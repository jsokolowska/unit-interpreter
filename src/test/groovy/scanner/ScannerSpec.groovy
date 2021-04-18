package scanner

import exception.ScannerException
import source.StringSource
import spock.lang.Specification
import util.Token.TokenType
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
    }
    def "Should recognize single operator tokens with unique prefixes"(){
        given:
        def str = "(){},;+-/^*"
        def source = new PositionWrapper(new StringSource(str))
        def scanner = new Scanner(source)
        def expected_types = [TokenType.OPEN_BRACKET, TokenType.CLOSE_BRACKET, TokenType.CURLY_OPEN, TokenType.CURLY_CLOSE,
                TokenType.COMMA, TokenType.SEMICOLON, TokenType.PLUS, TokenType.MINUS, TokenType.DIVIDE, TokenType.POWER,
                TokenType.MULTIPLY, TokenType.EOT]


        def token;
        expected_types.each{
            token = scanner.getToken()
            assert it == token.getTokenType()
        }

    }
    def "Should recognize double operators"(){
        given:
        def str = "= == != ! &&|| < > <= >="
        def scanner = new Scanner(new StringSource(str))
        def expected_types = [TokenType.ASSIGN, TokenType.EQUAL, TokenType.NOT_EQUAL, TokenType.NOT, TokenType.AND,
                TokenType.OR, TokenType.LESS, TokenType.GREATER, TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL, TokenType.EOT]
        def token;

        expected_types.each{
            token = scanner.getToken()
            assert it == token.getTokenType()
        }
    }
    def "Should recognize strings with double quotes"(){
        given:
        def str = "This is a string literal "
        def scanner = new Scanner(new StringSource("\"" + str + "\""))
        def token
        when:
        token = scanner.getToken()
        then:
        token.getTokenType() == TokenType.STRING
        token.getStringValue() ==  str

    }
    def "Should build number tokens"(){
        given:
        def scanner = new Scanner(new StringSource(str))
        when:
        def token = scanner.getToken()
        then:
        token.getTokenType() == TokenType.NUMERIC_LITERAL
        token.getFloatValue() == (Float) value
        where:
        str     || value
        "2745"  || 2745
        "0"     || 0
        "9.02"  || 9.02
        "0.008" || 0.008
        "0.0080"|| 0.008
    }
    def "Should not allow for nonzero number to start with zero"(){
        given:
        def str = "009"
        def scanner = new Scanner(new StringSource(str))
        when:
        scanner.getToken()
        then:
        thrown(ScannerException)

    }

   /* def "If second char of logical operator is missing throw ScannerException"(){
        setup:
        def scanner = new Scanner(new StringSource("&2" ))

        when:
        scanner.getToken()

        then:
        thrown(RuntimeException) Doesnt work for some reason
    }*/
}