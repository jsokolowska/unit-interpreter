package tree

import spock.lang.Specification
import tree.expression.Expression
import tree.expression.math.*
import tree.expression.operator.*

class ExpressionToStringSpec extends Specification {
    def "Check singular Expressions"(){
        when:
        expr.add(new Expression())

        then:
        expr.toString() == "u"

        where:
        expr <<[new AndExpression(), new OrExpression(), new PowerExpression(), new ExpressionWithOperators()]
    }

    def "Check singular UnaryExpression" (){
        given:
        def expr = new UnaryExpression()

        when:
        expr.add(new Expression(), op)

        then:
        expr.toString() == "[" + op + "u]"

        where:
        op <<[new NotOperator(), new NegOperator()]
    }

    def "Check multiple UnaryExpressions" () {
        given:
        def expr1 = new UnaryExpression()
        def expr2 = new UnaryExpression()

        when:
        expr2.add(new Expression(), op)
        expr1.add(expr2, op)

        then:
        expr1.toString() == "["+ op + "[" + op + "u" + "]" + "]"

        where:
        op <<[new NotOperator(), new NegOperator()]
    }

    def "Check multiple expressions with one operator type" (){
        when:
        for (int i= 0; i < 4; i++){
            expr.add(new Expression())
        }

        then:
        expr.toString() == str

        where:
        expr                    || str
        new OrExpression()      || "u||u||u||u"
        new AndExpression()     || "[u&&u&&u&&u]"
        new PowerExpression()   || "[u^u^u^u]"
    }

    def "Check multiple expressions with two operator types" (){
        when:
        var expr = new ExpressionWithOperators()
        expr.add(new Expression())
        expr.add(new Expression(), op1)
        expr.add(new Expression(), op2)

        then:
        expr.toString() == "[u" + op1 + "u" + op2 + "u]"

        where:
         op1                   | op2
         new PlusOperator()    | new MinusOperator()
         new DivOperator()     | new MulOperator()
         new EqOperator()      | new NotEqOperator()
    }

    def "Check multiple Relational Expression"(){
        given:
        def expr = new ExpressionWithOperators()

        when:
        expr.add(new Expression())
        expr.add(new Expression(), new GreaterEqOperator())
        expr.add(new Expression(), new GreaterOperator())
        expr.add(new Expression(), new LessEqOperator())
        expr.add(new Expression(), new LessOperator())

        then:
        expr.toString() == "[u>=u>u<=u<u]"
    }

}
