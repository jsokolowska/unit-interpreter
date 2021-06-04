package interpreter

import interpreter.env.Environment
import interpreter.util.Casting
import interpreter.util.StackValue
import parser.Parser
import scanner.Scanner
import source.StringSource
import spock.lang.Specification
import tree.Program
import tree.Variable
import tree.expression.math.AndExpression

import tree.expression.math.ExpressionWithOperators
import tree.expression.math.OrExpression
import tree.expression.math.PowerExpression
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
import tree.expression.operator.unit.UnitDivOperator
import tree.expression.operator.unit.UnitMinusOperator
import tree.expression.operator.unit.UnitMulOperator
import tree.expression.operator.unit.UnitPlusOperator
import tree.expression.operator.unit.UnitPowerOperator
import tree.expression.unit.ConversionExpression
import tree.expression.unit.MulUnitExpression
import tree.expression.unit.PowerUnitExpression
import tree.expression.unit.UnaryUnitExpression
import tree.expression.unit.value.UnitExpressionLiteral
import tree.expression.unit.value.UnitExpressionVariableValue
import tree.function.Arguments
import tree.function.Parameters
import tree.statement.AssignStatement
import tree.statement.BreakStatement
import tree.statement.ContinueStatement
import tree.statement.PrintStatement
import tree.statement.ReturnStatement
import tree.statement.TypeStatement
import tree.statement.VariableDeclarationStatement
import tree.type.BoolType
import tree.type.CompoundType
import tree.type.DoubleType
import tree.type.IntType
import tree.type.NumericType
import tree.type.StringType
import tree.type.TypeManager
import tree.type.UnitType
import tree.unit.CompoundExpr
import tree.value.Literal
import util.StringOutputStream
import util.exception.CastingException
import util.exception.InterpretingException

class InterpreterSpec extends Specification{

    static var bool_t = new BoolType()
    static var int_t = new IntType()
    static var double_t = new DoubleType()
    static var string_t = new StringType()

    static def prepEnv(){
        var env = new Environment()
        env.pushNewCallScope()
        env.pushNewBlock()
        return env
    }

    def static make_compound(ArrayList<String> terms, ArrayList<Integer> exponents){
        var expr = new CompoundExpr()
        var size = terms.size()
        for(int i=0; i<size; i++){
            expr.addPart(terms[i], exponents[i])
        }
        return new CompoundType(expr);
    }

    def "Should throw exception when program does not have main function"(){
        given:
        var interpreter = new Interpreter(new Program(), null)

        when:
        interpreter.execute()

        then:
        thrown(InterpretingException)
    }

    def "Should throw exception when a variable is redefined" (){
        given:
        var env = prepEnv()
        var variable = new Variable(new IntType(), "var")
        var interpreter = new Interpreter(new Program(), env)
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
        var interpreter = new Interpreter(new Program(),env)

        when:
        interpreter.visit(variable)

        then:
        variable == env.getVariable("var")
    }

    def "Check pushing literals to stack"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(new Program(), env)

        when:
        interpreter.visit(lit)
        var stackValue = env.popValue()

        then:
        stackValue.getValueAsLiteral() == lit
        stackValue.getType() == type


        where:
        lit <<[ new Literal<>("str"), new Literal<>(2), new Literal<>(10.2), new Literal<>(true)]
        type <<[new StringType(), new IntType(), new DoubleType(), new BoolType()]
    }

    def "Check visiting parameters"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(new Program(), env)
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
        "c"     | new DoubleType()
        "d"     | new UnitType("d")
        "e"     | new CompoundType("a", new CompoundExpr())
    }

    def "Check AndExpression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        AndExpression expr = new AndExpression()
        expr.add(new Literal<>(val1))
        expr.add(new Literal<>(val2))

        when:
        expr.accept(interpreter)
        def val = env.popValue().getValue()

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
        var interpreter = new Interpreter(null, env)
        var expr = new OrExpression()
        expr.add(new Literal<>(val1))
        expr.add(new Literal<>(val2))

        when:
        expr.accept(interpreter)
        def val = env.popValue().getValue()

        then:
        val == res

        where:
        val1    | val2                  || res
        true    | false                 || true
        1       | 9                     || true
        -3      | new Double(-9.9)      || false
    }

    def "Check ExpressionWithOperators for logical expression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var expr = new ExpressionWithOperators()
        expr.add(new Literal<>(val1))
        expr.add(new Literal<>(val2), op)

        when:
        expr.accept(interpreter)
        def val = env.popValue().getValue()

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
        var interpreter = new Interpreter(null, env)
        var expr = new ExpressionWithOperators()
        expr.add(new Literal<>(val1))
        expr.add(new Literal<>(val2), op)

        when:
        expr.accept(interpreter)
        def val = env.popValue().getValue()

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
        new Double(-1.5)    | -4                    | new MinusOperator()       || val1 - val2
        new Double(-0.5)    | 8                     | new MinusOperator()       || val1 - val2
        //multiplicative expressions
        2                   | 3                     | new MulOperator()         || val1 * val2
        0                   | -2                    | new MulOperator()         || val1 * val2
        new Double(-8.2)    | new Double(0.3)       | new MulOperator()         || val1 * val2
        new Double(-2)      | -9                    | new MulOperator()         || val1 * val2
        2                   | 3                     | new DivOperator()         || val1 / val2
        0                   | -9                    | new DivOperator()         || val1 / val2
        new Double(-1.2)    | new Double(0.3)       | new DivOperator()         || val1 / val2
        new Double(-3.02)   | -9                    | new DivOperator()         || val1 / val2
    }

    def "Check additive expression with operators for expressions with unit types"(){
        given:
        var env = prepEnv();
        var interpreter = new Interpreter(null, env)

        when:
        env.pushValue(new Literal<Object>(left), left_t)
        env.pushValue(new Literal<Object>(right), right_t)
        op.accept(interpreter)
        def stackVal = env.popValue().getValue()

        then:
        Math.abs(stackVal - res) < Casting.EPSILON

        where:
        left    | left_t            | right | right_t           | op                    || res
        2       | new UnitType("a") | 4     | left_t            | new PlusOperator()    || left + right
        4.3d    | new UnitType("a") | 4     | left_t            | new PlusOperator()    || left + right
        4.3d    | new UnitType("a") | 0.5d  | left_t            | new PlusOperator()    || left + right
        //multiplication
        2       | new UnitType("a") | 4     | left_t            | new MinusOperator()   || left - right
        4.3d    | new UnitType("a") | 4     | left_t            | new MinusOperator()   || left - right
        4.3d    | new UnitType("a") | 0.5d  | left_t            | new MinusOperator()   || left - right
    }

    def "Check multiplication with unit types"(){
        given:
        var env = prepEnv()
        var left_t = make_compound(["a", "b", "c"], [1,-2,1])
        var right_t = make_compound(["d", "c"], [8,-2])
        var expected_t = make_compound(["a", "b", "c", "d"], [1,-2,-1,8])
        var interpreter = new Interpreter(null, env)

        when:
        env.pushValue(new Literal<Object>(left), left_t)
        env.pushValue(new Literal<Object>(right), right_t)
        interpreter.visit(new MulOperator())
        def stackVal = env.popValue()

        then:
        Math.abs(stackVal.getValue() - left * right) < Casting.EPSILON
        stackVal.getType() == expected_t

        where:
        left    | right
        2       | 4
        2.3d    | 4
        -4      | 0.4d
    }

    def "Check division with unit types"(){
        given:
        var env = prepEnv()
        var left_t = make_compound(["a", "b", "c"], [1,-2,1])
        var right_t = make_compound(["d", "c"], [8,-2])
        var expected_t = make_compound(["a", "b", "c", "d"], [1,-2,3,-8])
        var interpreter = new Interpreter(null, env)

        when:
        env.pushValue(new Literal<Object>(left), left_t)
        env.pushValue(new Literal<Object>(right), right_t)
        interpreter.visit(new DivOperator())
        def stackVal = env.popValue()

        then:
        Math.abs(stackVal.getValue() - left / right) < Casting.EPSILON
        stackVal.getType() == expected_t

        where:
        left    | right
        2       | 4
        2.3d    | 4
        -4      | 0.4d
    }

    def "Check multiplicative operation with mixed types"(){
        given:
        var env = prepEnv()
        var left_t = make_compound(["a", "b", "c"], [1,-2,1])
        var interpreter = new Interpreter(null, env)

        when:
        env.pushValue(new Literal<Object>(left), left_t)
        env.pushValue(new Literal<Object>(right))
        op.accept(interpreter)
        def stackVal = env.popValue()

        then:
        Math.abs(stackVal.getValue() - res) < Casting.EPSILON
        stackVal.getType() == left_t

        where:
        left    | right     | op                || res
        2       | 4         | new MulOperator() || left * right
        2.3d    | 4         | new MulOperator() || left * right
        -4      | 0.4d      | new MulOperator() || left * right
        2       | 4         | new DivOperator() || left / right
        2.3d    | 4         | new DivOperator() || left / right
        -4      | 0.4d      | new DivOperator() || left / right
    }

    def "Check multiplicative division by units"(){
        given:
        var env = prepEnv()
        var right_t = make_compound(["a", "b", "c"], [1,-2,1])
        var interpreter = new Interpreter(null, env)

        when:
        env.pushValue(new Literal<Object>(left))
        env.pushValue(new Literal<Object>(right), right_t)
        op.accept(interpreter)
        def stackVal = env.popValue()

        then:
        Math.abs(stackVal.getValue() - res) < Casting.EPSILON
        stackVal.getType() == make_compound(["a", "b", "c"], [-1,2,-1])

        where:
        left    | right     | op                || res
        2       | 4         | new DivOperator() || left / right
        2.3d    | 4         | new DivOperator() || left / right
        -4      | 0.4d      | new DivOperator() || left / right
    }

    def "Check negative unary expression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var expr = new UnaryExpression()
        expr.add(new Literal<>(val), new NegOperator())

        when:
        expr.accept(interpreter)
        def stackVal = env.popValue().getValue()

        then:
        stackVal + val <= 0.0000001 && stackVal + val >= -0.0000001


        where:
        val             || res
        new Double(0.2) || -val
        9               || -val
    }

    def "Check negative unary expression for unit values"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null,env)
        var in_type = make_compound(["a", "b", "c"], [1,2,3])
        var stack_var = new StackValue(new Literal<Object>(val), in_type)

        when:
        env.pushValue(stack_var)
        interpreter.visit(new NegOperator())
        def stackVal = env.popValue()

        then:
        Math.abs(stackVal.getValue() - res) < Casting.EPSILON
        stackVal.getType() == in_type

        where:
        val             || res
        0.2d            || -val
        9               || -val
    }

    def "Check not unary expression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var expr = new UnaryExpression()
        expr.add(new Literal<>(val), new NotOperator())

        when:
        expr.accept(interpreter)
        def stackVal = env.popValue().getValue()

        then:
        stackVal == res

        where:
        val             || res
        true            || false
        false           || true
        2               || false
        -2              || true
        0               || true
        new Double(-9)  || true
        new Double(12)  || false
    }

    def "Check division errors"(){
        given:
        var env = prepEnv();
        var interpreter = new Interpreter(null, env)
        var expr = new ExpressionWithOperators();
        expr.add(new Literal(val1))
        expr.add(new Literal(val2), new DivOperator())

        when:
        expr.accept(interpreter);

        then:
        thrown(InterpretingException)

        where:
        val1    | val2
        22      | 0
        12      | new Double(0)
        "a"     | 12
        5.2     | "ll"
    }
    def "Check expression with operators exceptions"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var expr = new ExpressionWithOperators()
        expr.add(new Literal<>(val1))
        expr.add(new Literal<>(val2), op)

        when:
        expr.accept(interpreter)

        then:
        RuntimeException ex = thrown()
        ex instanceof CastingException || ex instanceof InterpretingException

        where:
        val1   | val2       | op
        12     | "-"        | new PlusOperator()
        "k"    | 6          | new MinusOperator()
        "w"    | "r"        | new DivOperator()
        -9     | "k"        | new PowerOperator()
        2      | "l"        | new MulOperator()
    }

    def "Check power expression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var expr = new PowerExpression()
        expr.add(new Literal<>(val1))
        expr.add(new Literal<>(val2))

        when:
        expr.accept(interpreter)
        def stackVal = env.popValue().getValue()

        then:
        Math.abs(stackVal - Math.pow(val1, val2)) < Casting.EPSILON

        where:
        val1            | val2
        1               | 3
        2.1d            | 0
        12              | -2
        1.5d            | 0.8d
    }

    def "Check power expression with unit variables"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null,  env)
        var left_t = make_compound(["a", "f", "r"], [1,-3, 2])
        env.pushValue(new StackValue(new Literal<Object>(val1), left_t))
        env.pushValue(new Literal<Object>(val2))

        when:
        interpreter.visit(new PowerOperator())
        def stackVal = env.popValue()
        left_t.exponentiate(val2)

        then:
        Math.abs(stackVal.getValue() - Math.pow(val1, val2)) < Casting.EPSILON
        stackVal.getType() == left_t

        where:
        val1            | val2
        1               | 3
        2.1d            | 0
        12              | -2
    }

    def "Check power expression with unit variables errors"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var left_t = make_compound(["a", "f", "r"], [1,-3, 2])
        env.pushValue(new StackValue(new Literal<Object>(val1), left_t))
        env.pushValue(new Literal<Object>(val2), exp_t)

        when:
        interpreter.visit(new PowerOperator())

        then:
        thrown(InterpretingException)

        where:
        val1            | val2      | exp_t
        1               | 3         | new UnitType("k")
        2.1d            | 0.1d      | double_t
    }

    def "Check power expression in unit conversion"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null,  env)
        env.pushValue(new StackValue(new Literal<Object>(left), left_t))
        env.pushValue(new Literal<Object>(right), right_t)

        when:
        op.accept(interpreter)
        def stackVal = env.popValue()

        then:
        Math.abs(stackVal.getValue() - res) < Casting.EPSILON
        stackVal.getType() instanceof NumericType

        where:
        left    | right   | left_t              | right_t                     | op                        || res
        2.0d    | 1.4d    | double_t            | new UnitType("a")           | new UnitPowerOperator()   || Math.pow(left, right)
        5.6d    | 2       | int_t               | make_compound(["l"], [2])   | new UnitPowerOperator()   || Math.pow(left, right)
        0       | 1.3d    | new UnitType("h")   | left_t                      | new UnitPowerOperator()   || Math.pow(left, right)
        // Plus
        -4      | 1.3d    | new UnitType("h")   | left_t                      | new UnitPlusOperator()    || left + right
        0.7d    | 1.3d    | new UnitType("h")   | make_compound(["l"], [2])   | new UnitPlusOperator()    || left + right
        0       | 13      | new UnitType("h")   | new UnitType("a")           | new UnitPlusOperator()    || left + right
        //Minus
        -3      | 1.3d    | new UnitType("h")   | left_t                      | new UnitMinusOperator()   || left - right
        0.7d    | 1.3d    | new UnitType("h")   | make_compound(["l"], [2])   | new UnitMinusOperator()   || left - right
        0       | 13      | new UnitType("h")   | new UnitType("a")           | new UnitMinusOperator()   || left - right
        //Multiply
        4       | 1.3d    | new UnitType("h")   | left_t                      | new UnitMulOperator()     || left * right
        0.7d    | 1.3d    | new UnitType("h")   | make_compound(["l"], [2])   | new UnitMulOperator()     || left * right
        1       | 13      | new UnitType("h")   | new UnitType("a")           | new UnitMulOperator()     || left * right
        //Divide
        1.3d    | 1.3d    | new UnitType("h")   | left_t                      | new UnitDivOperator()     || left / right
        0.7d    | 1.3d    | new UnitType("h")   | make_compound(["l"], [2])   | new UnitDivOperator()     || left / right
        10      | 13      | new UnitType("h")   | new UnitType("a")           | new UnitDivOperator()     || left / right
    }

    def "Check unit operator types"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        env.pushValue(new Literal<Object>(left))
        env.pushValue(new Literal<Object>(right))

        when:
        op.accept(interpreter)

        then:
        thrown(InterpretingException)

        where:
        left   | right  | op
        "a"    | 2      | new UnitDivOperator()
        "c"    | true   | new UnitDivOperator()
        "e"    | "k"    | new UnitDivOperator()

        "a"    | 2      | new UnitMulOperator()
        "c"    | true   | new UnitMulOperator()
        "e"    | "k"    | new UnitMulOperator()

        "a"    | 2      | new UnitPlusOperator()
        "c"    | false  | new UnitPlusOperator()
        true   | 4      | new UnitPlusOperator()

        "a"    | 2      | new UnitMinusOperator()
        "c"    | true   | new UnitMinusOperator()
        "e"    | "k"    | new UnitMinusOperator()

        "a"    | 2      | new UnitPowerOperator()
        "c"    | false  | new UnitPowerOperator()
        "e"    | "k"    | new UnitPowerOperator()
    }

    def "Check visit unit variable value"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var variable = new Variable(new UnitType("a"), "var")
        variable.setValue(new Literal<Object>(2))
        var unit_var_value = new UnitExpressionVariableValue("var")

        when:
        env.addVariable(variable)
        interpreter.visit(unit_var_value)

        then:
        env.popValue().getValue() == 2
    }

    def "Check visit unit variable value error"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var unit_var_value = new UnitExpressionVariableValue("var")

        when:
        interpreter.visit(unit_var_value)

        then:
        thrown(InterpretingException)
    }

    def "Check visit unit literal"() {
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var lit = new UnitExpressionLiteral(15)

        when:
        interpreter.visit(lit)

        then:
        env.popValue().getValue() == 15
    }

    def "Check visit unary unit expression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var expr = new UnaryUnitExpression()
        expr.add(new UnitExpressionLiteral<>(value))

        when:
        interpreter.visit(expr)

        then:
        Math.abs(env.popValue().getValue() + value) < Casting.EPSILON

        where:
        value <<[12, 0, -4, -9.4d, 8.5d]
    }

    def "Check visit power unit expression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var expr = new PowerUnitExpression()
        expr.add(new UnitExpressionLiteral(1))
        expr.add(new UnitExpressionLiteral(5.7d))
        expr.add(new UnitExpressionLiteral(8.3d))

        when:
        expr.accept(interpreter)

        then:
        def res = env.popValue().getValue()
        Math.abs(res - 1) < Casting.EPSILON
    }

    def "Check visit mul unit expression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var expr = new MulUnitExpression()
        expr.add(new UnitExpressionLiteral(1))
        expr.add(new UnitExpressionLiteral(5.7d), new UnitMulOperator())
        expr.add(new UnitExpressionLiteral(8.3d), new UnitDivOperator())

        when:
        expr.accept(interpreter)

        then:
        def res = env.popValue().getValue()
        Math.abs(res - 1 * 5.7d / 8.3d) < Casting.EPSILON
    }

    def "Check visit conversion expression"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var conv_expr = new ConversionExpression();
        var expr = new MulUnitExpression()
        expr.add(new UnitExpressionLiteral(1.05d))
        expr.add(new UnitExpressionLiteral(7.3d), new UnitMulOperator())
        conv_expr.add(expr);
        conv_expr.add(new UnitExpressionLiteral(12), new UnitMinusOperator())

        when:
        conv_expr.accept(interpreter)

        then:
        def res = env.popValue().getValue()
        Math.abs(res - 1.05d * 7.3d + 12) < Casting.EPSILON
    }

    def "Check visit print statement"(){
        given:
        var env = prepEnv()
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(null, env, new PrintStream(outStream))
        var expr1 = new Literal<>(val1)
        var expr2 = new Literal<>(val2)
        var args = new Arguments()
        args.addArgument(expr1)
        args.addArgument(expr2)
        var print_stmt = new PrintStatement(args)

        when:
        print_stmt.accept(interpreter)

        then:
        outStream.getStringValue() == res_val

        where:
        val1       | val2       || res_val
        12         | "ttt"      || "12, ttt\n"
        -4         | "#-o"      || "-4, #-o\n"
        "aa"       | "bbbb"     || "aa, bbbb\n"
    }

    def "Check visit return statement with no return value"() {
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var print_stmt = new ReturnStatement()

        when:
        print_stmt.accept(interpreter)

        then:
        env.hasReturned()
    }

    def "Check continue statement"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var stmt = new ContinueStatement()

        when:
        stmt.accept(interpreter)

        then:
        env.hasContinued()
    }

    def "Check break statement"(){
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var stmt = new BreakStatement()

        when:
        stmt.accept(interpreter)

        then:
        env.hasBroken()
    }

    def "Check assign statement"() {
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var stmt = new AssignStatement("identifier", new Literal(12))
        var variable = new Variable(new IntType(), "identifier")
        env.addVariable(variable)

        when:
        stmt.accept(interpreter)

        then:
        variable.getValue().getLiteralValue() == 12
    }

    def "Check var declaration statement"() {
        given:
        var env = prepEnv()
        var interpreter = new Interpreter(null, env)
        var stmt = new VariableDeclarationStatement(int_t, "identifier")

        when:
        stmt.accept(interpreter)

        then:
        env.getVariable("identifier")
    }

    def "Check function call statement"() {
        given:
        var scanner = new Scanner(new StringSource("int f(int t){return 12+t;} int main(){ print(f(5)); return 3;}"))
        var parser = new Parser(scanner)
        var env = prepEnv()
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(parser.parse(), env, new PrintStream(outStream))

        when:
        interpreter.execute()

        then:
        outStream.getStringValue() == "17\n"
    }

    def "Check while statement"() {
        given:
        var scanner = new Scanner(new StringSource("int main(){ int k = 0; while(k < 10){ k= k+1;} return 3;}"))
        var parser = new Parser(scanner)
        var env = prepEnv()
        var interpreter = new Interpreter(parser.parse(), env)

        when:
        interpreter.execute()

        then:
        noExceptionThrown()
    }

    def "Check if else statement"() {
        given:
        var scanner = new Scanner(new StringSource("int main(){if(" + cond +"){print(\"if\");}else{print(\"else\");}return 2;}"))
        var parser = new Parser(scanner)
        var env = prepEnv()
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(parser.parse(),env, new PrintStream(outStream))

        when:
        interpreter.execute()

        then:
        outStream.getStringValue() == res

        where:
        cond    || res
        "true"  || "if\n"
        "false" || "else\n"
    }

    def "Check visit conversion function "(){
        given:
        var str = "let kilogram as (meter m) { 10 * m + 1}; int main(){meter met = 2; kilogram k = kilogram(met); print(k); return 2;}"
        var scanner = new Scanner(new StringSource(str))
        var parser = new Parser(scanner)
        var env = prepEnv()
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(parser.parse(),  env, new PrintStream(outStream))

        when:
        interpreter.execute()

        then:
        outStream.getStringValue() == "21 [unit kilogram]\n"
    }

    def "Check call scopes"(){
        given:
        var str = "int f(){int i=10; print(i); return 2;} int main(){int i=0; f(); return 4;}";
        var scanner = new Scanner(new StringSource(str))
        var parser = new Parser(scanner)
        var env = prepEnv()
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(parser.parse(),  env, new PrintStream(outStream))

        when:
        interpreter.execute()

        then:
        outStream.getStringValue() == "10\n"
    }

    def "Check function calls and return values"(){
        given:
        var scanner = new Scanner(new StringSource(str))
        var parser = new Parser(scanner)
        var env = prepEnv()
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(parser.parse(),  env, new PrintStream(outStream))

        when:
        interpreter.execute()

        then:
        outStream.getStringValue() == "2\n4\n"

        where:
        str << ["int f(){return 2;} int main(){ print(f()); f(); print(4); return 0;}",
                "void f(){print(2); print(4);} int main(){f(); return 0;}",
                "void f(){print(2); print(4); return;} int main(){f(); return 0;}"]
    }

    def "Check break, continue and return inside of while statement"(){
        given:
        var scanner = new Scanner(new StringSource(str))
        var parser = new Parser(scanner)
        var env = prepEnv()
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(parser.parse(),  env, new PrintStream(outStream))

        when:
        interpreter.execute()

        then:
        outStream.getStringValue() == res

        where:
        str                                                                                 || res
        "int main(){while(true){print(2); break; print(3);} return 0;}"                     || "2\n"
        "int main(){while(true){print(2); return -1; print(3);} return 0;}"                 || "2\n"
        "int main(){int i=0; while(i<3){i = i+1; if(i == 2){continue;} print(i);}return 0;}"|| "1\n3\n"
    }

    def "Check provided conversions"(){
        given:
        var scanner = new Scanner(new StringSource(str))
        var parser = new Parser(scanner)
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(parser.parse(), new Environment(), new PrintStream(outStream))

        when:
        interpreter.execute()

        then:
        outStream.getStringValue() == res

        where:
        str                                                                                 || res
        "unit k; int main(){k k_var = 12.1; print(int(k_var)); return 0;}"                  || "12\n"
        "unit k; int main(){k k_var = 12.5; print(float(k_var)); return 0;}"                || "12.5\n"
    }

    def "Check function calls for overloaded functions"(){
        given:
        def str = "void a(int k){print(\"a(int)\");} void a(float k){print(\"a(float)\");} " +
                "int main(){int i =0; float f = 0; a(i); a(f); return 1;}"
        var scanner = new Scanner(new StringSource(str))
        var parser = new Parser(scanner)
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(parser.parse(), new Environment(), new PrintStream(outStream))

        when:
        interpreter.execute()

        then:
        outStream.getStringValue() == "a(int)\na(float)\n"
    }

    def "Check function calls for overloaded conversions"(){
        given:
        def str = "let meter as (kilogram k) {2}; let meter as (second s){3};" +
                "int main(){kilogram k=1; second s=1; print(meter(s)); print(meter(k)); return 0;}"
        var scanner = new Scanner(new StringSource(str))
        var parser = new Parser(scanner)
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(parser.parse(), new Environment(), new PrintStream(outStream))

        when:
        interpreter.execute()

        then:
        outStream.getStringValue() == "3 [unit meter]\n2 [unit meter]\n"
    }

    def "Check interpreting errors"(){
        given:
        var parser = new Parser(new Scanner(new StringSource(str)))
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(parser.parse(),  new Environment(), new PrintStream(outStream))

        when:
        interpreter.execute()

        then:
        thrown(InterpretingException)

        where:
        str     <<["int main(){int(12, 3); return 0;}",
                   "int main(){k(12); return 0; }",
                   "int f(){} int main(){f(19); return 0;}",
                   "int main(){}",
                   "int main(){return;}",
                    "int main() {return k+12;}",
                    "int m(){return 0;}",
                    "int main() { m = 2; return 0;}",
                    "int main(){ int i = - true; return 0;}",
                    "int main(){int i = true/false; return 0;}"]
    }

    def "Check inferred types"() {
        given:
        var parser = new Parser(new Scanner(new StringSource(str)))
        var outStream = new StringOutputStream()
        var interpreter = new Interpreter(parser.parse(), new Environment(), new PrintStream(outStream))

        when:
        interpreter.execute()

        then:
        res.each {
            outStream.getStringValue().contains(it)
        }

        where:
        str << ["unit joule as  <kilogram * meter^2 / second ^2>; int main(){compound c; joule j = 12; c = j; type(j); return 0;} "]
        res << [["compound joule", "kilogram^1", "meter^2", "second^-2"]]
    }
}
