package tree

import spock.lang.Specification
import tree.type.UnitType
import tree.unit.CompoundExpr
import tree.unit.CompoundTerm

class CompoundExpressionSpec extends Specification{

    def "Check adding new parts to CompoundExpression"(){
        given:
        def expr = new CompoundExpr()
        def part = new CompoundTerm( new UnitType("newton"), exp)

        when:
        expr.addPart(part)

        then:
        expr.size() == 1
        expr.contains(part)

        where:
        exp || K
        -15 || 0
        0   || 0
        12  || 0
    }

    def "Check adding new parts to CompoundExpression when one part of the same type already exists"(){
        given:
        def unit = new UnitType("newton")
        def expr = new CompoundExpr()
        def part1 = new CompoundTerm( unit, exp1)
        def part2 = new CompoundTerm( unit, exp2)

        when:
        expr.addPart(part1)
        expr.addPart(part2)

        then:
        expr.size() == 1
        expr.contains(new CompoundTerm(unit, exp1+exp2))

        where:
        exp1 || exp2
        -15  || 12
        0    || 9
        12   || -8
        2    || -2
    }

    def "Check if simplify removes units with exponent equal to 0"(){
        given:
        def unit1 = new UnitType("newton")
        def unit2 = new UnitType("newton2")
        def expr = new CompoundExpr()

        when:
        expr.addPart(new CompoundTerm(unit1, 0))
        expr.addPart(new CompoundTerm(unit2, 19))
        expr.simplify()

        then:
        expr.size() == 1
        expr.contains(new CompoundTerm(unit2, 19))
    }
}
