package parser

import scanner.Scanner
import source.StringSource
import spock.lang.Specification
import tree.function.Parameters
import tree.statement.BreakStatement
import tree.statement.ContinueStatement
import tree.statement.ReturnStatement
import tree.type.TypeManager
import tree.type.UnitType
import tree.unit.CompoundTerm
import tree.unit.UnitDeclaration
import util.exception.ParserException

class ParserSpec extends Specification{

    static def prepareParser(str){
       new Parser (new Scanner(new StringSource(str)))
    }

    static def prepareParser(str, manager){
        new Parser (new Scanner(new StringSource(str)), manager)
    }

    def "Should parse different return statements"(){
        given: "Parser that uses string source"
        def parser = prepareParser(str)
        def result

        when: "trying to parse Statement"
        result = parser.parseStatement()

        then: "A not null instance of ReturnStatement class is parsed"
        result != null
        result instanceof ReturnStatement

        where:
        str << ["return ;" , "return 2;", "return _a;", "return 2+2;"]
    }

    def "Should throw scanner exceptions for improper return statement"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseStatement()

        then:
        ParserException p = thrown()
        p.message.contains(msg_str)

        where:
        str         || msg_str
        "return "   || "Expected literal, function call or variable reference"
        "return 3"  || "SEMICOLON"
    }

    def "Should parse break" (){
        given:
        def parser = prepareParser("break;")
        def result

        when:
        result = parser.parseStatement()

        then:
        assert result != null
        assert result instanceof BreakStatement
    }

    def "Should parse continue"(){
        given:
        def parser = prepareParser("continue;")
        def result

        when:
        result = parser.parseStatement()

        then:
        assert result != null
        assert result instanceof ContinueStatement
    }

    def "Should throw parsing util.exception for missing semicolons in break and continue statements" (){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseStatement()

        then:
        thrown(ParserException)

        where:
        str << ["break", "continue"]

    }

    def "Should parse compound terms" (){
        given:
        def parser = prepareParser(str)
        def res

        when:
        res = parser.parseOneCompoundTerm()

        then:
        assert res.getUnitName() == name
        assert res.getExponent() == exponent

        where:
        str         || name         | exponent
        "meter ^ 2" || "meter"      | 2
        "kilogram"  || "kilogram"   | 1
        "second"    || "second"     | 1
        "second^-3" || "second"     | -3
    }

    def "Should throw util.exception for incorrect compound terms"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseOneCompoundTerm()

        then:
        ParserException ex = thrown()
        ex.message.contains(msg_str)

        where:
        str         || msg_str
        "second^0"  || "cannot be 0"
        "9^3"       || "unit type"
        "a^2"       || "Unit usage before definition"
        "second^a"  || "INT_LITERAL"
        "meter^-b"  || "INT_LITERAL"
    }

    def "Should parse compound expressions" () {
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseCompoundExpression()

        then:
        parts.each{
            assert result.contains(it)
        }

        where:
        str                                     || parts
        "<second ^ 2>"                          || [new CompoundTerm(new UnitType("second"), 2)]

        "<kilogram ^2 / meter ^4 * second^2>"   || [new CompoundTerm(new UnitType("kilogram"), 2),
                                                    new CompoundTerm(new UnitType("second"), -2),
                                                    new CompoundTerm(new UnitType ("meter"), -4)]

        "<second ^ 2 * meter^ 3 / kilogram ^4>" || [new CompoundTerm(new UnitType("second"), 2),
                                                    new CompoundTerm(new UnitType("meter"), 3),
                                                    new CompoundTerm(new UnitType("kilogram"), -4)]
        "<kilogram ^2 / meter ^4 * kilogram^1>" || [new CompoundTerm(new UnitType("kilogram"), 1),
                                                    new CompoundTerm(new UnitType ("meter"), -4)]

        "<kilogram ^2 * kilogram^2 / meter ^4>" || [new CompoundTerm(new UnitType("kilogram"), 4),
                                                    new CompoundTerm(new UnitType ("meter"), -4)]
    }

    def "Should simplify compound expressions"(){
        given:
        def parser = prepareParser("<kilogram ^2 / meter ^4 * kilogram^2>" )
        def result
        def right = new CompoundTerm(new UnitType("meter"), -4)
        def wrong = new CompoundTerm(new UnitType("kilogram"), 0)

        when:
        result = parser.parseCompoundExpression()

        then:
        result.contains(right)
        !result.contains(wrong)
    }

    def "Should throw Parser util.exception for improper compound expressions"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseCompoundExpression()

        then:
        ParserException ex = thrown()
        ex.message.contains(msg_str)

        where:
        str                 || msg_str
        "<meter^3"          || "GREATER"
        "<>"                || "must contain at least one term"
        "<meter^2/meter^2>" || "equivalent to empty"
    }

    def "Should parse compound unit declarations "(){
        given:
        def manager = new TypeManager()
        def parser = prepareParser(str, manager)
        def result
        def expr

        when:
        result = parser.parseUnitDeclaration()
        expr = result.getType()

        then:
        result instanceof UnitDeclaration
        result.getUnitName() == name
        parts.each{
            assert expr.contains(it)
        }
        and:
        manager.exists(name)

        where:
        str                                                 || name     | parts
        "unit k as <second ^ 2>;"                            || "k"      | [new CompoundTerm(new UnitType("second"), 2)]
        "unit m2 as <kilogram ^2 / meter ^4>;"               || "m2"     | [new CompoundTerm(new UnitType("kilogram"), 2),
                                                                           new CompoundTerm(new UnitType ("meter"), -4)]
        "unit a_a as <second ^ 2 * meter^ 3 / kilogram ^4>;"|| "a_a"    | [new CompoundTerm(new UnitType("second"), 2),
                                                                           new CompoundTerm(new UnitType("meter"), 3),
                                                                           new CompoundTerm(new UnitType("kilogram"), -4)]
    }

    def "Should parse base unit declarations" (){
        given:
        def manager = new TypeManager()
        def parser = prepareParser("unit newton;", manager)
        def result

        when:
        result = parser.parseUnitDeclaration()

        then:
        result instanceof UnitDeclaration
        result.getUnitName() == "newton"
        and:
        manager.exists("newton")
    }

    def "Should throw util.exception if parsing an improper unit declaration" (){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseUnitDeclaration()

        then:
        ParserException ex = thrown()
        ex.message.contains(msg_str)

        where:
        str             || msg_str
        "unit as <a>"   || "IDENTIFIER"
        "unit k as i"   || "LESS"
    }

    def "Should throw util.exception when parsing improper parameter"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseParameter(new Parameters())

        then:
        ParserException ex = thrown()
        ex.message.contains(msg_str)

        where:
        str     || msg_str
        "int 2" || "IDENTIFIER"
        "2 k"   || "Expected type"
        "k uui" || "Type usage before definition"
    }

    def "Should parse parameters"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseParameters()

        then:
        result.toString() == param_str

        where:
        str                         || param_str
        "(int val)"                 || "(int:val)"
        "(second s, meter k)"       || "(second:s,meter:k)"
        "(meter k, int a, int b)"   || "(meter:k,int:a,int:b)"
        "()"                        || "(none)"
    }

    def "Should throw util.exception when parsing improper parameters"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseParameters()

        then:
        ParserException ex = thrown()
        ex.message.contains(msg_str)

        where:
        str                         ||   msg_str
        "(int i"                    || "CLOSE_BRACKET"
    }

    def "Should parse different values as unit expression"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseUnitExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str         || resStr
        "10.02"     || "10.02"
        "5"         || "5"
        "key"       || "key"
    }

    def "Should parse simple UnaryUnitExpression" (){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseUnaryUnitExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str     || resStr
        "-1"    || "[-1]"
        "-10.04"|| "[-10.04]"
        "-tt"   || "[-tt]"
    }

    def "Should parse simple PowerUnitExpression" (){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parsePowerUnitExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str         || resStr
        "2^2"       || "[2^2]"
        "key^-2"     || "[key^[-2]]"
    }

    def "Should parse simple MulUnitExpression" (){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseMulUnitExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str         || resStr
        "2*-4/key"  ||"[2*[-4]/key]"
        "2"         || "2"
        "10/2"      || "[10/2]"
    }

    def "Should parse different ConversionExpressions"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseConversionExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str                   || resStr
        "k^2-8"               || "[k^2]-8"
        "2 + 3*i/(-16*w)^2"   || "2+[3*i/[[[-16]*w]^2]]"
        "2+ 2 + 3*i^2"        || "2+2+[3*[i^2]]"
        "2 + 2"               || "2+2"
        "2-3/5^ke"            || "2-[3/[5^ke]]"
    }

    def "Should throw ParserException when provided with improper unit expressions"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseConversionExpression()

        then:
        ParserException ex = thrown()
        ex.message.contains(msg_str)

        where:
        str             || msg_str
        "()"            || "Empty parenthesis"
        "(-2"           || "CLOSE_BRACKET"
        "\"str\""       || "number literal or variable"
    }

    def "Should parse unit conversion"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseUnitConversion()

        then:
        result.toString() == res_str

        where:
        str                                 || res_str
        "let meter as (kilogram k){k^2-8};" || "meter:(kilogram:k)->{[k^2]-8}"
    }

    def "Should throw Parser Exception when provided with improper unit conversion"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseUnitConversion()

        then:
        ParserException ex = thrown()
        ex.message.contains(msg)

        where:
        str                                 || msg
        "let 9"                             || "Expected unit type"
        "let k"                             || "Unit usage before definition"
        "let meter ("                       || "AS"
        "let meter as k"                    || "OPEN_BRACKET"
        "let meter as (kilogram k)k^2"      || "CURLY_OPEN"
        "let meter as (kilogram k){k^2-8}"  || "SEMICOLON"
        "let meter as (kilogram k){k^2-8;"  || "CURLY_CLOSE"

    }

    def "Should parse simple math expression values" (){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str         || resStr
        "true"      || "true"
        "false"     || "false"
        "a"         || "a"
        "\"s\""     || "s"
        "12"        || "12"
        "10.2"      || "10.2"
        "(2)"       || "2"
    }

    def "Should throw ParserException on improper math expression values"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseExpression()

        then:
        ParserException ex = thrown()
        ex.message.contains(msg)

        where:
        str         || msg
        "(i"        || "CLOSE_BRACKET"
        "meter"     || "literal, function call or variable reference"
    }

    def "Should parse simple UnaryExpression"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseUnaryExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str         || resStr
        "-2"        || "[-2]"
        "!false"    || "[!false]"
    }

    def "Should parse PowerExpression" (){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parsePowerExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str     || resStr
        "2^-5"  || "[2^[-5]]"
        "2^key" || "[2^key]"
        "2^4^-5"|| "[2^4^[-5]]"
        "5"     || "5"
    }

    def "Should parse MultiplyExpression" (){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseMultiplyExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str         || resStr
/*        "5*9"       || "[5*9]"*/
        "7/12/3"    || "[7/12/3]"
        "6^-22/8"   || "[[6^[-22]]/8]"
        "6^2^3^1*1" || "[[6^2^3^1]*1]"
    }

    def "Should parse ArithmeticExpression" (){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseArithmeticExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str     || resStr
        "2+15"  || "[2+15]"
        "0-8+-7"|| "[0-8+[-7]]"
    }

    def "Should parse Relational Expression"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseRelationalExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str         || resStr
        "1>2"       || "[1>2]"
        "0<=key"    || "[0<=key]"
        "-2>0"      || "[[-2]>0]"
        "true >=0"  || "[true>=0]"
        "6^3>!2*3"  || "[[6^3]>[[!2]*3]]"
    }

    def "Should parse ComparisonExpression"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseComparisonExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str             || resStr
        "1==0"          || "[1==0]"
        "0!=true"       || "[0!=true]"
        "0*8==-19^-3"   || "[[0*8]==[[-19]^[-3]]]"
    }

    def "Should parse AndExpression"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseAndExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str         || resStr
        "1&&2"      || "[1&&2]"
        "-9&&false" || "[[-9]&&false]"
        "0>8&&-7"   || "[[0>8]&&[-7]]"
    }

    def "Should parse OrExpression"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseOrExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str                 || resStr
        "key||1"            ||"key||1"
        "2>7||true==false"  ||"[2>7]||[true==false]"
        "2^4^-5||9"         ||"[2^4^[-5]]||9"
    }

    def "Should parse complicated Expressions"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseOrExpression()

        then:
        result != null
        result.toString() == resStr

        where:
        str                     || resStr
        "-(-(-(-(-(-key)))))"   ||"[-[-[-[-[-[-key]]]]]]"
        "1&&7-(-(-(-3)))"       ||"[1&&[7-[-[-[-3]]]]]"
        "x+y-i^7*12-9/6"        ||"[x+y-[[i^7]*12]-[9/6]]"
        "(x+y)*17"              ||"[[x+y]*17]"
        "a*a+(a-a)"             ||"[[a*a]+[a-a]]"
    }

    def "Should parse function call"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseFunctionCall()

        then:
        result.toString() == res_str

        where:
        str             || res_str
        "aa(2)"         || "aa(2)"
        "k()"           || "k(none)"
        "k(8+12, 0, -5)"|| "k([8+12], 0, [-5])"
        "meter(6)"      || "meter(6)"
        "int(12.5)"     || "int(12.5)"
        "float(2)"      || "float(2)"
    }

    def "Should throw ParserException error on improper function calls"() {
        given:
        def parser = prepareParser(str)

        when:
        parser.parseFunctionCall()

        then:
        ParserException ex = thrown()
        ex.message.contains(msg_str)

        where:
        str     || msg_str
        "k(9"   || "CLOSE_BRACKET"
        "k(1 5" || "CLOSE_BRACKET"
    }

    def "Should parse Arguments"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseArguments()

        then:
        result.toString() == res_str

        where:
        str             || res_str
        "(1,2)"         || "1, 2"
        "(8^3, ko)"     || "[8^3], ko"
        "(k(k(k())))"   || "k(k(k(none)))"
        "(7, k())"      || "7, k(none)"
        "()"            || "none"
    }

    def "Should parse loop"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseStatement()

        then:
        result.toString() == res_str

        where:
        str             || res_str
        "while(true){}" ||"while(true):{}"
        "while(2)return;"||"while(2):return:null"
    }

    def "Should throw ParserException when given improper loop"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseStatement()

        then:
        ParserException ex = thrown()
        ex.message.contains(msg_str)

        where:
        str             || msg_str
        "while a"       || "OPEN_BRACKET"
        "while(2) 12"   || "CURLY_OPEN"
        "while(true i"  || "CLOSE_BRACKET"
    }

    def "Should parse Type Statement"(){
        given:
        def parser = prepareParser("type(k);" )
        def result

        when:
        result = parser.parseStatement()

        then:
        result.toString() ==  "type:k"
    }
    def "Should throw ParserException when given improper type statement"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseStatement()

        then:
        ParserException ex = thrown()
        ex.message.contains(msg_str)

        where:
        str             || msg_str
        "type(k)"       || "SEMICOLON"
        "type 9"        || "OPEN_BRACKET"
        "type(uu"       || "CLOSE_BRACKET"
    }
    def "Should parse function call statement"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseStatement()

        then:
        result.toString() == res_str

        where:
        str             || res_str
        "aa(2);"         || "aa(2)"
        "k();"           || "k(none)"
        "k(8+12, 0, -5);"|| "k([8+12], 0, [-5])"
        "meter(6);"      || "meter(6)"
    }

    def "Should throw parser util.exception for missing semicolon in select statements"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseStatement()

        then:
        ParserException ex = thrown()
        ex.message.contains("SEMICOLON")

        where:
        str <<["k()", "print()", "print(a)"]
    }

    def "Should parse print statement"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseStatement()

        then:
        result.toString() == res_str

        where:
        str                 || res_str
        "print();"          || "print(none)"
        "print(\"aaa\");"   || "print(aaa)"
        "print(2,14);"      || "print(2, 14)"
    }

    def "Should parse if else statement"(){
        given:
        def parser = prepareParser(str)

        when:
        def result = parser.parseStatement()

        then:
        result.toString() == msg_str

        where:
        str                             || msg_str
        "if(2) return;"                 || "if(2)<return:null>\nelse<null>"
        "if(2) return 2; else continue;"|| "if(2)<return:2>\nelse<continue>"
        "if(i<3)print(2);"              || "if([i<3])<print(2)>\nelse<null>"
    }
    def "Should not parse improper if else statement"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseStatement()

        then:
        thrown(ParserException)

        where:
        str <<["if(2)else", "if(2){}else;"]

    }

    def "Should throw Parser Exception when given improper assign statement"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseAssignStatement()

        then:
        ParserException ex = thrown()
        ex.message.contains(expected)

        where:
        str                 || expected
        "k=2"               || "SEMICOLON"
        "k=;"               || "literal, function call or variable reference"
    }

    def "Should parse assign statement"(){
        given:
        def parser = prepareParser(str)

        when:
        def result = parser.parseStatement()

        then:
        result.toString() == res_str

        where:
        str                  || res_str
        "k=2;"               || "k=2"
        "k= 7+128;"          || "k=[7+128]"
        "k= true&&false^2;"  || "k=[true&&[false^2]]"
    }

    def "Should parse var declaration statement"(){
        given:
        def parser = prepareParser(str)

        when:
        def result = parser.parseStatement()

        then:
        result.toString() == res_str

        where:
        str                 || res_str
        "int k;"            || "int:k=null"
        "second s = 12^2;"  || "second:s=[12^2]"
        "k k = 2;"          || "null"
        "compound c;"       || "inferred:c=null"
        "compound c = 2;"   || "inferred:c=2"
    }

    def "Should throw ParserException for improper var declaration"(){
        given:
        def parser = prepareParser(source)

        when:
        parser.parseStatement()

        then:
        ParserException ex = thrown()
        ex.message.contains(expected)

        where:
        source              || expected
        "int k"             || "SEMICOLON"
        "second s s"        || "SEMICOLON"
        "second s =s+2"     || "SEMICOLON"
        "second s =;"       || "literal, function call or variable reference"
    }

    def "Should parse block statements"(){
        given:
        def parser = prepareParser(str)

        when:
        def result = parser.parseStatement()

        then:
        result.toString() == res_str

        where:
        str                                     || res_str
        "{int k;}"                              || "{int:k=null\n}"
        "{second s = 12^2; int k;}"             || "{second:s=[12^2]\nint:k=null\n}"
        "{return 3; break ; continue;}"         || "{return:3\nbreak\ncontinue\n}"
        "{{{}}}"                                || "{{{}\n}\n}"
        "{type(k);return 0;}"                   || "{type:k\nreturn:0\n}"
        "{print(\"a\"); return 0;}"             || "{print(a)\nreturn:0\n}"
        "{if(2)return; else return; return;}"   || "{if(2)<return:null>\nelse<return:null>\nreturn:null\n}"
    }

    def "Should throw Parser Exception for improper block statement"(){
        given:
        def parser = prepareParser("{ return;")

        when:
        parser.parse()

        then:
        thrown(ParserException)
    }

    def "Should parse functions"(){
        given:
        def parser = prepareParser(str)

        when:
        def result = parser.parse()

        then:
        result != null

        where:
        str << ["int main(){}",
                "float k(int i){return i;}",
                "meter m2(string aa, int k){}",
                "void f( int i){}"]
    }

    def "Should throw parser util.exception for improper function definition"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parse()

        then:
        ParserException ex = thrown()
        ex.message.contains(msg_str)

        where:
        str                             || msg_str
        "a kkkk(){}"                    || "Type usage before definition"
        "int k({}"                      || "Expected type"
        "int k(int)"                    || "IDENTIFIER"
        "float a(int i){"               || "CURLY_CLOSE"
        "float(){}"                     || "IDENTIFIER"
    }

    def "Should parse good Program strings"(){
        given:
        def parser = prepareParser(str)


        expect:
        parser.parse() != null


        where:
        str <<[ "int main (){}",
                "unit k; int main (){}",
                "unit a as <meter^2/kilogram^2>; int main(){}",
                "unit a; unit b; let b as (a aaa){aaa+2}; a k(){}",
                "int main(){} meter m(){}",
                "int a(){} int b(){} int x(){}",
                "int m(int k, float z){return true +2;}"

        ]
    }

    def "Should not parse bad Program strings"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parse()

        then:
        thrown(ParserException)

        where:
        str <<[ "int main (){",
                "unit k; unit k; int main (){}",
                " ",
                "unit a; unit b; k(){} let b as (a aaa){aaa+2};",
                "int main(){} meter m(){} unit b;",
                "int a(){} int a(){}"
        ]
    }
}
