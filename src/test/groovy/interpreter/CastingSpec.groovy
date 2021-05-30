package interpreter

import interpreter.util.Casting
import interpreter.util.StackValue
import spock.lang.Specification
import tree.type.BoolType
import tree.type.CompoundType
import tree.type.DoubleType
import tree.type.IntType
import tree.type.StringType
import tree.type.UnitType
import tree.unit.CompoundExpr
import tree.value.Literal
import util.exception.CastingException
import util.exception.InterpretingException

class CastingSpec extends Specification{

    Casting casting = new Casting(new Integer(2))
    static var bType = new BoolType()
    static var iType = new IntType()
    static var dType = new DoubleType()
    static var sType = new StringType()

    def "Cast to boolean"(){
        expect:
        casting.cast(stackVal, bType).getValue() ==  val

        where:
        stackVal                                                || val
        new StackValue(new Literal<>(true), bType)              || true
        new StackValue(new Literal<>(false), bType)             || false
        new StackValue(new Literal<>(2), iType)                 || true
        new StackValue(new Literal<>(-3), iType)                || false
        new StackValue(new Literal<>(new Double(12.67)), dType) || true
        new StackValue(new Literal<>(new Double(-9.03)), dType) || false
    }

    def "Check casting exceptions"(){
        given:
        var stack_val = new StackValue(new Literal<Object>(lit), type)

        when:
        casting.cast(stack_val, dest_type);

        then:
        thrown(CastingException)

        where:
        lit     | type          || dest_type
        "k"     | sType         || bType
        "a"     | sType         || iType
        "z"     | sType         || dType
        2.03f   | dType         || iType

    }

    def "Cast to integer"(){
        given:
        def stack_val = new StackValue(new Literal<Object>(lit), type)

        when:
        def ret_val = casting.cast(stack_val, iType)

        then:
        ret_val.getValue() instanceof Integer
        ret_val.getValue() == res_val
        ret_val.getType() instanceof IntType

        where:
        lit               | type                || res_val
        new Double(2.3)   | dType               || 2
        new Double(9.9)   | dType               || 10
        new Double(3.1)   | new UnitType("a")   || 3
    }


    def "Cast to double"(){
        given:
        def stack_val = new StackValue(new Literal<Object>(lit), type)

        when:
        def ret_val = casting.cast(stack_val, dType)

        then:
        ret_val.getValue() instanceof Double
        Math.abs(((Double) ret_val.getValue())-lit) < Casting.EPSILON
        ret_val.getType() instanceof DoubleType

        where:
        lit  | type
        14   | iType
        -9   | iType
        1    | new UnitType("a")
    }

    def "Check multiply for base units"(){
        when:
        CompoundType res = casting.multiplyUnitTypes(new UnitType("k"), new UnitType("a")) as CompoundType

        then:
        res.size() == 2
        res.contains("k", 1)
        res.contains("a", 1)
    }

    def "Check multiply for base unit and compound"(){
        given:
        def expr = new CompoundExpr()
        expr.addPart("a", 18)
        expr.addPart("b", -9)
        expr.addPart("k", 2)
        def compound = new CompoundType("compound", expr)

        when:
        CompoundType res = casting.multiplyUnitTypes(new UnitType("k"), compound) as CompoundType

        then:
        res.size() == 3
        res.contains("k", 3)
        res.contains("a", 18)
        res.contains("b", -9)
    }

    def "Check multiply for two compounds"(){
        given:
        def expr1 = new CompoundExpr()
        expr1.addPart("a", 12)
        expr1.addPart("b", -2)
        def compound1 = new CompoundType("-", expr1)

        def expr2 = new CompoundExpr()
        expr2.addPart("c", 8)
        expr2.addPart("a", -8)
        def compound2 = new CompoundType("-", expr2)

        when:
        def res = casting.multiplyUnitTypes(compound1, compound2) as CompoundType

        then:
        res.size() == 3
        res.contains("a", 4)
        res.contains("b", -2)
        res.contains("c", 8)
    }

    def "Calculate type for division with two compounds"(){
        given:
        def expr1 = new CompoundExpr()
        expr1.addPart("a", 12)
        expr1.addPart("b", -2)
        def compound1 = new CompoundType("-", expr1)

        def expr2 = new CompoundExpr()
        expr2.addPart("c", 8)
        expr2.addPart("a", -8)
        def compound2 = new CompoundType("-", expr2)

        when:
        def res = casting.calculateTypeForDivision(compound1, compound2) as CompoundType

        then:
        res.size() == 3
        res.contains("a", 20)
        res.contains("b", -2)
        res.contains("c", -8)
    }

    def "Calculate type for division with unit denominator"(){
        when:
        def res = casting.calculateTypeForDivision(new IntType(), new UnitType("i")) as CompoundType

        then:
        res.size() == 1
        res.contains("i", -1)
    }

    def "Calculate type for division with unit numerator"(){
        when:
        def res = casting.calculateTypeForDivision( new UnitType("i"), new IntType()) as UnitType

        then:
        res.getName() == "i"
    }

    def "Calculate type for two ints"(){
        when:
        def res = casting.calculateTypeForDivision(new IntType(), new IntType())

        then:
        res instanceof IntType
    }

    def "Calculate type for int and float"(){
        when:
        def res = casting.calculateTypeForDivision(new IntType(), new DoubleType())

        then:
        res instanceof DoubleType
    }

    def "Calculate type for division with two base units"(){
        when:
        def res = casting.calculateTypeForDivision(new UnitType("a"), new UnitType("b")) as CompoundType

        then:
        res.size() == 2
        res.contains("a", 1)
        res.contains("b", -1)
    }

    def "Check errors for division casting"(){
        when:
        casting.calculateTypeForDivision(new StringType(), new IntType())

        then:
        thrown(InterpretingException)
    }

    def "Check calculating types for multiplication"(){
        when:
        def res = casting.calculateTypeForMultiplication(first, second)

        then:
        res == expected

        where:
        first   | second    || expected
        dType   | iType     || dType
        iType   | dType     || dType
        iType   | iType     || iType
    }
    def "Check calculating unit types for multiplication"(){
        when:
        def res = casting.calculateTypeForMultiplication(first, second)

        then:
        res == expected

        where:
        first                                       | second                || expected
        new UnitType("a")                           | dType                 || new UnitType("a")
        new UnitType("a")                           | iType                 || new UnitType("a")
        new UnitType("a")                           | new UnitType("b")     || make_compound(["a", "b"], [1,1])
        make_compound(["a", "b", "c"], [1,1,1])     | dType                 || first
        iType                                       | make_compound(["a", "b", "c"], [1,1,1]) || second
        make_compound(["a", "b", "c"], [1,1,1])     | make_compound(["a", "b", "d"], [8,1,-3])
                || make_compound(["a", "b", "c", "d"], [9,2,1,-3])
    }

    def static make_compound(ArrayList<String> terms, ArrayList<Integer> exponents){
        var expr = new CompoundExpr()
        var size = terms.size()
        for(int i=0; i<size; i++){
            expr.addPart(terms[i], exponents[i])
        }
        return new CompoundType("name", expr);
    }
}
