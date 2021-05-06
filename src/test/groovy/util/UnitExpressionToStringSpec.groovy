package util


import spock.lang.Specification
import util.tree.expression.operator.DivOperator
import util.tree.expression.operator.MinusOperator
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

    def "Check singular ConversionExpression"(){
        given:
        def obj = new ConversionExpression()

        when:
        obj.add(new UnitExpression())

        then:
        obj.toString() == "u"
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
        def obj = new PowerUnitExpression()

        when:
        obj.add(new UnitExpression())
        obj.add(new UnitExpression())
        obj.add(new UnitExpression())

        then:
        obj.toString() == "[u^u^u]"
    }

    def "Check multiple UnaryUnitExpression"(){
        given:
        def obj = new UnaryUnitExpression()
        def obj2 = new UnaryUnitExpression()
        def obj3 = new UnaryUnitExpression()
        def obj4 = new UnitExpression()

        when:
        obj3.add(obj4)
        obj2.add(obj3)
        obj.add(obj2)

        then:
        obj.toString() == "[-[-[-u]]]"
    }

    def "Check multiple ConversionExpression"(){
        given:
        def obj = new ConversionExpression();

        when:
        obj.add(new UnitExpression());
        obj.add(new UnitExpression(), new PlusOperator())
        obj.add(new UnitExpression(), new MinusOperator())

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
        obj6.add(new UnitExpression())
        obj6.add(new UnitExpression(), new MulOperator())
        obj5.add(obj6)
        obj4.add(new UnitExpression())
        obj4.add(obj5)
        obj4.add(new UnitExpression())
        obj3.add(obj4)
        obj3.add(new UnitExpression(), new MulOperator())
        obj2.add(new UnitExpression())
        obj2.add(new UnitExpression(), new DivOperator())
        obj1.add(obj2)
        obj1.add(obj3, new PlusOperator())

        then:
        obj1.toString() == "[u/u]+[[u^[-[u*u]]^u]*u]"

    }

}
