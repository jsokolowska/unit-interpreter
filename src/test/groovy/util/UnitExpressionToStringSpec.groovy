package util


import spock.lang.Specification
import util.tree.expression.Expression
import util.tree.expression.operator.DivOperator
import util.tree.expression.operator.MinusOperator
import util.tree.expression.operator.MulOperator
import util.tree.expression.operator.PlusOperator
import util.tree.expression.unit.ConversionExpression
import util.tree.expression.unit.MulUnitExpression
import util.tree.expression.unit.PowerUnitExpression
import util.tree.expression.unit.UnaryUnitExpression

class UnitExpressionToStringSpec extends Specification{

    def "Check singular UnitExpression"(){
        given:
        def obj = new Expression()

        expect:
        obj.toString() == "u"
    }

    def "Check singular MulUnitExpression"(){
        given:
        def obj = new MulUnitExpression()

        when:
        obj.add(new Expression())

        then:
        obj.toString() == "u"
    }

    def "Check singular PowerUnitExpression"(){
        given:
        def obj = new PowerUnitExpression()

        when:
        obj.add(new Expression())

        then:
        obj.toString() == "u"
    }

    def "Check singular UnaryUnitExpression"(){
        given:
        def obj = new UnaryUnitExpression()

        when:
        obj.add(new Expression())

        then:
        obj.toString() == "[-u]"
    }

    def "Check singular ConversionExpression"(){
        given:
        def obj = new ConversionExpression()

        when:
        obj.add(new Expression())

        then:
        obj.toString() == "u"
    }

    def "Check multiple MulUnitExpressions" (){
        given:
        def obj = new MulUnitExpression()

        when:
        obj.add(new Expression())
        obj.add(new Expression(), new MulOperator())
        obj.add(new Expression(), new DivOperator())

        then:
        obj.toString() == "[u*u/u]"
    }

    def "Check multiple PowerUnitExpresions" (){
        given:
        def obj = new PowerUnitExpression()

        when:
        obj.add(new Expression())
        obj.add(new Expression())
        obj.add(new Expression())

        then:
        obj.toString() == "[u^u^u]"
    }

    def "Check multiple UnaryUnitExpression"(){
        given:
        def obj = new UnaryUnitExpression()
        def obj2 = new UnaryUnitExpression()
        def obj3 = new UnaryUnitExpression()
        def obj4 = new Expression()

        when:
        obj3.add(obj4)
        obj2.add(obj3)
        obj.add(obj2)

        then:
        obj.toString() == "[-[-[-u]]]"
    }

    def "Check multiple ConversionExpression"(){
        given:
        def obj = new ConversionExpression()

        when:
        obj.add(new Expression())
        obj.add(new Expression(), new PlusOperator())
        obj.add(new Expression(), new MinusOperator())

        then:
        obj.toString() == "u+u-u"
    }

    def "Check complicated expression"(){
        given:
        def obj1 = new ConversionExpression()
        def obj2 = new MulUnitExpression()
        def obj3 = new MulUnitExpression()
        def obj4 = new PowerUnitExpression()
        def obj5 = new UnaryUnitExpression()
        def obj6 = new MulUnitExpression()

        when:
        obj6.add(new Expression())
        obj6.add(new Expression(), new MulOperator())
        obj5.add(obj6)
        obj4.add(new Expression())
        obj4.add(obj5)
        obj4.add(new Expression())
        obj3.add(obj4)
        obj3.add(new Expression(), new MulOperator())
        obj2.add(new Expression())
        obj2.add(new Expression(), new DivOperator())
        obj1.add(obj2)
        obj1.add(obj3, new PlusOperator())

        then:
        obj1.toString() == "[u/u]+[[u^[-[u*u]]^u]*u]"

    }

}
