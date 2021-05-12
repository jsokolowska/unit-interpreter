package parser

import exception.ParserException
import scanner.Scanner
import source.StringSource
import spock.lang.Specification
import util.tree.statement.BreakStatement
import util.tree.statement.ContinueStatement
import util.tree.statement.ReturnStatement
import util.tree.type.IntType
import util.tree.type.UnitType
import util.tree.unit.CompoundTerm
import util.tree.unit.UnitDeclaration

class ParserSpec extends Specification{

    static def prepareParser(str){
        return new Parser (new Scanner(new StringSource(str)))
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
        str << ["return ;"] //, "return 2;", "return _a;"] will not work until expresion parsing is done
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

    def "Should throw parsing exception for missing semicolons" (){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseStatement()

        then:
        thrown(ParserException)

        where:
        str << ["break", "continue", "return"]

    }

    def "Should parse compound terms" (){
        given:
        def parser = prepareParser(str)
        def res

        when:
        res = parser.parseOneCompoundTerm()

        then:
        assert res.getUnitType().getName() == name
        assert res.getExponent() == exponent

        where:
        str         || name         | exponent
        "meter ^ 2" || "meter"      | 2
        "kilogram"  || "kilogram"   | 1
        "second"    || "second"     | 1

    }

    def "Should throw exception for incorrect compound terms"(){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseOneCompoundTerm()

        then:
        thrown(ParserException)

        where:
        str << ["second^0", "9^3", "meter^-9"]
    }

    def "Should parsecompound expressions" () {
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

    def "Should parse unit declarations "(){
        given:
        def parser = prepareParser(str)
        def result
        def expr

        when:
        result = parser.parseUnitDeclaration()
        expr = result.getType()

        then:
        result instanceof UnitDeclaration
        result.getName() == name
        parts.each{
            assert expr.contains(it)
        }

        where:
        str                                                 || name     | parts
        "unit k as<second ^ 2>;"                            || "k"      | [new CompoundTerm(new UnitType("second"), 2)]
        "unit m2 as<kilogram ^2 / meter ^4>;"               || "m2"     | [new CompoundTerm(new UnitType("kilogram"), 2),
                                                                           new CompoundTerm(new UnitType ("meter"), -4)]
        "unit a_a as <second ^ 2 * meter^ 3 / kilogram ^4>;"|| "a_a"    | [new CompoundTerm(new UnitType("second"), 2),
                                                                           new CompoundTerm(new UnitType("meter"), 3),
                                                                           new CompoundTerm(new UnitType("kilogram"), -4)]
    }

    def "Should throw exception if parsing an improper unit declaration" (){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseUnitDeclaration()

        then:
        thrown(ParserException)

        where:
        str <<["unit as <a>", "unit k as i"]
    }

    def "Should parse parameters"(){
        given:
        def parser = prepareParser(str)
        def result
        def len = types.size()

        when:
        result = parser.parseParameters()

        then:
        for (int i=0; i<len; i++){
            result.contains(names[i], types[i])
        }

        where:
        str                         ||   types                                                  | names
        "(int val)"                 || [new IntType()]                                          |["val"]
        "(second s, meter k)"       || [new UnitType("second"), new UnitType("meter")]          |["s", "k"]
        "(meter k, int a, int b)"   || [new UnitType("meter"), new IntType(), new IntType()]    |["k", "a", "b"]
    }

    def "Should parse different values as unit expression"(){
        given:
        def parser = prepareParser(str)
        def result;

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
        def parser = prepareParser(str);
        def result;

        when:
        result = parser.parseUnaryUnitExpression()

        then:
        result != null;
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
        "2 + 3*i/(-16*w)^2"   || "2+[3*i/[[[-16]*w]^2]]"
        "2+ 2 + 3*i^2"        || "2+2+[3*[i^2]]"
        "2 + 2"               || "2+2"
        "2-3/5^ke"            || "2-[3/[5^ke]]"
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
        "a()"       || "a(none)"
        "a"         || "a"
        "\"s\""     || "s"
        "12"        || "12"
        "10.2"      || "10.2"
    }

    def "Should parse simple UnaryExpression"(){
        given:
        def parser = prepareParser(str);
        def result;

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
        def parser = prepareParser(str);
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
        def parser = prepareParser(str);
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
        def parser = prepareParser(str);
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
        def parser = prepareParser(str);
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
        def parser = prepareParser(str);
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
        def parser = prepareParser(str);
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
        def parser = prepareParser(str);
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

    def "Parse complicated Expressions"(){
        given:
        def parser = prepareParser(str);
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


}
