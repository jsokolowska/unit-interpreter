package tree

import spock.lang.Specification
import tree.type.UnitType
import tree.unit.CompoundExpr
import tree.unit.CompoundTerm

class UnitTypeSpec extends Specification {

    def "Should recognize empty expressions as equal"(){
        given: "two empty compound expressions"
        def one = new CompoundExpr()
        def two = new CompoundExpr()

        expect: "them to be equal"
        one == two
        two == one
    }

    def "Should recognize non-empty identical expressions as equal"(){
        given: "two empty compound expressions"
        def one = new CompoundExpr()
        def two = new CompoundExpr()

        when: "the same parts are added"
        one.addPart(new CompoundTerm(new UnitType("newton"), 2))
        one.addPart(new CompoundTerm(new UnitType("newton2"), 17))

        two.addPart(new CompoundTerm(new UnitType("newton"), 2))
        two.addPart(new CompoundTerm(new UnitType("newton2"), 17))

        then: "they are equal"
        one == two
        two == one
    }

    def "Should recognize non-empty compound expressions that need simplyfing as equal"(){
        given: "two empty compound expressions"
        def one = new CompoundExpr()
        def two = new CompoundExpr()

        when: "the same parts are added"
        one.addPart(new CompoundTerm(new UnitType("newton"), 2))
        one.addPart(new CompoundTerm(new UnitType("newton2"), 17))
        one.addPart(new CompoundTerm(new UnitType("k"), 0))

        two.addPart(new CompoundTerm(new UnitType("newton"), 2))
        two.addPart(new CompoundTerm(new UnitType("newton2"), 17))

        then: "they are equal"
        one == two
        two == one
    }

    def "Should recognize non-empty compound expressions with parts that need simplyfing as equal"(){
        given: "two empty compound expressions"
        def one = new CompoundExpr()
        def two = new CompoundExpr()

        when: "the same parts are added"
        one.addPart(new CompoundTerm(new UnitType("newton"), 2))
        one.addPart(new CompoundTerm(new UnitType("newton2"), 12))
        one.addPart(new CompoundTerm(new UnitType("newton2"), 5))

        two.addPart(new CompoundTerm(new UnitType("newton"), 2))
        two.addPart(new CompoundTerm(new UnitType("newton2"), 17))

        then: "they are equal"
        one == two
        two == one
    }

    def "Should not recognize non-empty compound expressions with parts that need simplyfing as equal"(){
        given: "two empty compound expressions"
        def one = new CompoundExpr()
        def two = new CompoundExpr()

        when: "the same parts are added"
        one.addPart(new CompoundTerm(new UnitType("newton"), 2))
        one.addPart(new CompoundTerm(new UnitType("newton2"), 12))
        one.addPart(new CompoundTerm(new UnitType("newton2"), 7))

        two.addPart(new CompoundTerm(new UnitType("newton"), 2))
        two.addPart(new CompoundTerm(new UnitType("newton2"), 17))

        then: "they are not equal"
        one != two
        two != one
    }

    def "Should recognize UnitTypes and CompoundExpressions as equal"(){
        given:
        def unitType = new UnitType("newton")
        def expr = new CompoundExpr()

        when:
        expr.addPart(new CompoundTerm(unitType, 1))

        then:
        expr.equals(unitType)
        unitType.equals(expr)
    }

    def "Should not recognize UnitTypes and CompoundExpressions as equal"(){
        given:
        def unitType = new UnitType("newton")
        def expr = new CompoundExpr()

        when:
        expr.addPart(new CompoundTerm(new UnitType("k"), 1))

        then:
        ! expr.equals(unitType)
        ! unitType.equals(expr)
    }

    def "Should compare UnitTypes and CompoundExpressions when CompoundExpression needs simplyfying"(){
        given:
        def unitType = new UnitType("newton")
        def expr = new CompoundExpr()

        when:
        expr.addPart(new CompoundTerm(new UnitType("kk"), -192))
        expr.addPart(new CompoundTerm(unitType, 1))

        then:
        ! expr.equals(unitType)
        ! unitType.equals(expr)
    }

}
