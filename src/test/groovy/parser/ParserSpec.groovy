package parser

import exception.ParserException
import source.StringSource
import scanner.Scanner
import spock.lang.*
import util.tree.statement.BreakStatement
import util.tree.statement.ContinueStatement
import util.tree.statement.ReturnStatement
import util.tree.type.UnitType
import util.tree.unit.UnitDeclaration
import util.tree.unit.CompoundTerm


class ParserSpec extends Specification{

    static def prepareParser(str){
        return new Parser (new Scanner(new StringSource(str)))
    }

    def "Should parse different return statements"(){
        given: "Parser that uses string source"
        def parser = prepareParser(str)
        def result;

        when: "trying to parse Statement"
        result = parser.parseStatements()

        then: "A not null instance of ReturnStatement class is parsed"
        result != null;
        result instanceof ReturnStatement

        where:
        str << ["return ;"] //, "return 2;", "return _a;"] will not work until expresion parsing is done
    }

    def "Should parse break" (){
        given:
        def parser = prepareParser("break;")
        def result;

        when:
        result = parser.parseStatements();

        then:
        result != null
        result instanceof BreakStatement
    }

    def "Should parse continue"(){
        given:
        def parser = prepareParser("continue;")
        def result;

        when:
        result = parser.parseStatements();

        then:
        result != null
        result instanceof ContinueStatement
    }

    def "Should throw parsing exception for missing semicolons" (){
        given:
        def parser = prepareParser(str)

        when:
        parser.parseStatements();

        then:
        thrown(ParserException)

        where:
        str << ["break", "continue", "return"]

    }
    def "Should parse compound terms" (){
        given:
        def parser = prepareParser(str)
        def res;

        when:
        res = parser.parseOneCompoundTerm();

        then:
        res.getUnitType().getName() == name
        res.getExponent() == exponent

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
        parser.parseOneCompoundTerm();

        then:
        thrown(ParserException)

        where:
        str << ["second^0", "9^3", "meter^-9"]
    }

    def "Should parse compound expressions" () {
        given:
        def parser = prepareParser(str)
        def result;

        when:
        result = parser.parseCompoundExpression();

        then:
        parts.each{
            result.contains(it)
        }

        where:
        str                                     || parts
        "<second ^ 2>"                          || [new CompoundTerm(new UnitType("second"), 2)]

        "<kilogram ^2 / meter ^4 * second^2>"   || [new CompoundTerm(new UnitType("kilogram"), 2), new CompoundTerm(new UnitType("second"), -2),
                                                    new CompoundTerm(new UnitType ("meter"), -4)]

        "<second ^ 2 * meter^ 3 / kilogram ^4>" || [new CompoundTerm(new UnitType("second"), 2), new CompoundTerm(new UnitType("meter"), 3),
                                                    new CompoundTerm(new UnitType("meter"), -4)]
    }
    def "Should parse unit declarations "(){
        given:
        def parser = prepareParser(str)
        def result;
        def expr;

        when:
        result = parser.parseUnitDeclaration();
        expr = result.getType()

        then:
        result instanceof UnitDeclaration
        result.getName() == name
        parts.each{
            expr.contains(it)
        }

        where:
        str                                                 || name     | parts
        "unit k as<second ^ 2>;"                            || "k"      | [new CompoundTerm(new UnitType("second"), 2)]
        "unit m2 as<kilogram ^2 / meter ^4>;"               || "m2"     | [new CompoundTerm(new UnitType("kilogram"), 2),
                                                                           new CompoundTerm(new UnitType ("meter"), -4)]
        "unit a_a as <second ^ 2 * meter^ 3 / kilogram ^4>;"|| "a_a"    | [new CompoundTerm(new UnitType("second"), 2), new CompoundTerm(new UnitType("meter"), 3),
                                                                           new CompoundTerm(new UnitType("meter"), -4)]
    }

    def "Should throw exception if parsing an improper unit declaration" (){
        given:
        def parser = prepareParser(str)
        def result;

        when:
        result = parser.parseUnitDeclaration();

        then:
        thrown(ParserException)

        where:
        str <<["unit as <a>", "unit k as i"]
    }

    /*def "Should parse argument list"(){
        given:
        def parser = prepareParser(str)
        def result

        when:
        result = parser.parseArgList();

        then:
        result.getType() == type
        result.getName() == name

        where:
        str         ||   type    | name
        "int val"   || "int"    | "val"
        "second s"  || "second" | "s"
        "kkk k"     || "kkk"    | "k"
    }*/


}
