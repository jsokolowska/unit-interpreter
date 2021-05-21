package tree

import spock.lang.Specification
import tree.type.UnitType
import tree.unit.CompoundExpr
import tree.unit.CompoundTerm

class UnitTypeEqualitySpec extends Specification {

    def "Should recognize empty expressions as equal"(){
        given: "two empty compound expressions"
        def one = new CompoundExpr()
        def two = new CompoundExpr()

        expect: "them to be equal"
        one == two
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
    }

}
