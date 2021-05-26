package interpreter

import interpreter.env.Environment
import spock.lang.Specification
import tree.Program
import tree.Variable
import tree.expression.math.AndExpression

import tree.expression.math.ExpressionWithOperators
import tree.expression.math.OrExpression
import tree.expression.math.UnaryExpression
import tree.expression.operator.DivOperator
import tree.expression.operator.EqOperator
import tree.expression.operator.GreaterEqOperator
import tree.expression.operator.GreaterOperator
import tree.expression.operator.LessEqOperator
import tree.expression.operator.LessOperator
import tree.expression.operator.MinusOperator
import tree.expression.operator.MulOperator
import tree.expression.operator.NegOperator
import tree.expression.operator.NotEqOperator
import tree.expression.operator.NotOperator
import tree.expression.operator.PlusOperator
import tree.expression.operator.PowerOperator
import tree.function.Parameters
import tree.type.BoolType
import tree.type.CompoundType
import tree.type.FloatType
import tree.type.IntType
import tree.type.StringType
import tree.type.UnitType
import tree.unit.CompoundExpr
import tree.value.Literal
import util.exception.InterpretingException

class InterpreterSpec extends Specification{

    def "Should throw exception when program does not have main function"(){
        given:
        var interpreter = new Interpreter(new Program(), null, null)

        when:
        interpreter.execute()

        then:
        thrown(InterpretingException)
    }

    static def prepEnv(){
        var env = new Environment()
        env.pushNewCallScope()
        env.pushNewBlock()
        return env
    }

    def "Should throw exception when a variable is redefined" (){
        given:
        var env = prepEnv()
        var variable = new Variable(new IntType(), "var")
        var interpreter = new Interpreter(new Program(), null, env)
        env.addVariable(variable)

        when:
        interpreter.visit(variable)

        then:
        thrown(InterpretingException)
    }

    def "Should add variable to the stack when visiting it"(){
        given:
        var env = prepEnv()
        var variable = new Variable(new IntType(), "var")
        var interpreter = new Interpreter(new Program(), null, env)

        when:
        interpreter.visit(variable)

        then:
        variable == env.getVariable("var")
    }

    def "Check pushing literals to stack"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(new Program(), null, env)

        when:
        interpreter.visit(lit)
        var stackValue = env.popValue()

        then:
        stackValue.getValue() == lit
        stackValue.getType() == type


        where:
        lit <<[ new Literal<>("str"), new Literal<>(2), new Literal<>(10.2), new Literal<>(true)]
        type <<[new StringType(), new IntType(), new FloatType(), new BoolType()]
    }

    def "Check visiting parameters"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(new Program(), null, env)
        var params = new Parameters()
        params.addParameter(str, type)

        when:
        params.accept(interpreter)

        then:
        var variable = env.getVariable(str)
        variable.getIdentifier() ==  str
        variable.getType() == type

        where:
        str     | type
        "a"     | new IntType()
        "b"     | new BoolType()
        "c"     | new FloatType()
        "d"     | new UnitType("d")
        "e"     | new CompoundType("a", new CompoundExpr())
    }

    def "Check AndExpression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, null, env)
        AndExpression expr = new AndExpression()
        expr.add(new Literal<>(val1))
        expr.add(new Literal<>(val2))

        when:
        expr.accept(interpreter)
        def val = env.popValue().getValue().getLiteralValue()

        then:
        val == res

        where:
        val1    | val2                  || res
        true    | false                 || false
        1       | 9                     || true
        -3      | new Double(-9.9)      || false
    }

    def "Check OrExpression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, null, env)
        var expr = new OrExpression()
        expr.add(new Literal<>(val1))
        expr.add(new Literal<>(val2))

        when:
        expr.accept(interpreter)
        def val = env.popValue().getValue().getLiteralValue()

        then:
        val == res

        where:
        val1    | val2                  || res
        true    | false                 || true
        1       | 9                     || true
        -3      | new Double(-9.9)      || false
    }

    def "Check ExpressionWithOperators  for logical expression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, null, env)
        var expr = new ExpressionWithOperators()
        expr.add(new Literal<>(val1))
        expr.add(new Literal<>(val2), op)

        when:
        expr.accept(interpreter)
        def val = env.popValue().getValue().getLiteralValue()

        then:
        val == res

        where:
        val1                | val2                  | op                        || res
        //relational expression
        1                   | 9                     | new GreaterOperator()     || false
        10                  | 9                     | new GreaterOperator()     || true
        new Double(2)       | new Double(-2)        | new GreaterOperator()     || true
        new Double(2.8)     | 9                     | new GreaterOperator()     || false
        2                   | 3                     | new GreaterEqOperator()   || false
        -3                  | new Double(-9.9)      | new LessEqOperator()      || false
        0                   | 8                     | new LessOperator()        || true
        //comparison expression
        12                  | 12                    | new EqOperator()          || true
        new Double(12.09)   | new Double(12.09)     | new EqOperator()          || true
        2                   | "str"                 | new NotEqOperator()       || true
    }

    def "Check expression with operators for expressions with numeric values"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, null, env)
        var expr = new ExpressionWithOperators()
        expr.add(new Literal<>(val1))
        expr.add(new Literal<>(val2), op)

        when:
        expr.accept(interpreter)
        def val = env.popValue().getValue().getLiteralValue()

        then:
        val - res <= 0.0000001 && val-res >= -0.0000001

        where:
        val1                | val2                  | op                        || res
        //additive expression
        12                  | -4                    | new PlusOperator()        || val1 + val2
        new Double(-9.2)    | 6                     | new PlusOperator()        || val1 + val2
        new Double(2)       | new Double(5)         | new PlusOperator()        || val1 + val2
        -9                  | -9                    | new MinusOperator()       || val1 - val2
        new Double(-1.5)    | new Double(8)         | new MinusOperator()       || val1 - val2
        //multiplicative expressions
        2                   | 3                     | new MulOperator()         || val1 * val2
        0                   | -2                    | new MulOperator()         || val1 * val2
        new Double(-8.2)    | new Double(0.3)       | new MulOperator()         || val1 * val2
        new Double(-2)      | -9                    | new MulOperator()         || val1 * val2
        2                   | 3                     | new DivOperator()         || val1 / val2
        0                   | -9                    | new DivOperator()         || val1 / val2
        new Double(-1.2)    | new Double(0.3)       | new DivOperator()         || val1 / val2
        new Double(-3.02)   | -9                    | new DivOperator()         || val1 / val2
        //power expression
        2                   | -4                    | new PowerOperator()       || Math.pow(val1, val2)
        new Double(-2.3)    | 0                     | new PowerOperator()       || Math.pow(val1, val2)
    }

    def "Check negative unary expression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, null, env)
        var expr = new UnaryExpression()
        expr.add(new Literal<>(val), new NegOperator())

        when:
        expr.accept(interpreter)
        def stackVal = env.popValue().getValue().getLiteralValue()

        then:
        stackVal + val <= 0.0000001 && stackVal + val >= -0.0000001


        where:
        val             || res
        new Double(0.2) || -val
        9               || -val
    }

    def "Check not unary expression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, null, env)
        var expr = new UnaryExpression()
        expr.add(new Literal<>(val), new NotOperator())

        when:
        expr.accept(interpreter)
        def stackVal = env.popValue().getValue().getLiteralValue()

        then:
        stackVal == res

        where:          //!val
        val             || res
        true            || false
        false           || true
        2               || false
        -2              || true
        0               || true
        new Double(-9)  || true
        new Double(12)  || false
    }

}
