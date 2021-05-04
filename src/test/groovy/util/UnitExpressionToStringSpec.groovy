package util

import spock.lang.Specification
import util.tree.expression.operator.DivOperator
import util.tree.expression.operator.MulOperator
import util.tree.expression.operator.PlusOperator
import util.tree.expression.unit.ConversionExpression
import util.tree.expression.unit.MulUnitExpression
import util.tree.expression.unit.PowerUnitExpression
import util.tree.expression.unit.UnaryUnitExpression
import util.tree.expression.unit.UnitExpression

class UnitExpressionToStringSpec extends Specification{

    def "Check singular UnitExpression"(){
        given:
        def obj = new UnitExpression()

        expect:
        obj.toString() == "u"
    }

    def "Check singular MulUnitExpression"(){
        given:
        def obj = new MulUnitExpression()

        when:
        obj.add(new UnitExpression())

        then:
        obj.toString() == "u"
    }

    def "Check singular PowerUnitExpression"(){
        given:
        def obj = new PowerUnitExpression()

        when:
        obj.add(new UnitExpression())

        then:
        obj.toString() == "u"
    }

    def "Check singular UnaryUnitExpression"(){
        given:
        def obj = new UnaryUnitExpression()

        when:
        obj.add(new UnitExpression())

        then:
        obj.toString() == "[-u]"
    }

    def "Check multiple MulUnitExpressions" (){
        given:
        def obj = new MulUnitExpression()

        when:
        obj.add(new UnitExpression())
        obj.add(new UnitExpression(), new MulOperator())
        obj.add(new UnitExpression(), new DivOperator())

        then:
        obj.toString() == "[u*u/u]"
    }

    def "Check multiple PowerUnitExpresions" (){
        given:
        def obj = new PowerUnitExpression();

        when:
        obj.add(new UnitExpression())
        obj.add(new UnitExpression())
        obj.add(new UnitExpression())

        then:
        obj.toString() == "[u^u^u]"
    }


}
