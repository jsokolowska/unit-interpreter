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
    static var bool_t = new BoolType()
    static var int_t = new IntType()
    static var double_t = new DoubleType()
    static var string_t = new StringType()

    def "Cast to string"(){
        given:
        var stackVal = new StackValue(new Literal<>(obj), type)

        expect:
        casting.cast(stackVal, string_t).getType() == string_t
        print(casting.cast(stackVal, string_t).getValue())

        where:
        obj     | type
        true    | bool_t
        float   | bool_t
        2       | int_t
        1.12d   | double_t
        12.0d   | new UnitType("l")
        6       | make_compound(["a", "b"], [1,-2])
    }

    def "Cast to boolean"(){
        given:
        var stackVal = new StackValue(new Literal<>(obj), type)

        expect:
        casting.cast(stackVal, bool_t).getValue() ==  val

        where:
        obj     |type                          || val
        true    | bool_t                       || true
        false   | bool_t                       || false
        2       | int_t                        || true
        -3      | int_t                        || false
        12.67d  | double_t                     || true
        -9.03d  | double_t                     || false
        3       | new UnitType("a")            || true
        -2.09d  | make_compound(["a"], [3])    || false
    }

    def "Cast to integer"(){
        given:
        def stack_val = new StackValue(new Literal<Object>(lit), type)

        when:
        def ret_val = casting.cast(stack_val, int_t)

        then:
        ret_val.getValue() instanceof Integer
        ret_val.getValue() == res_val
        ret_val.getType() instanceof IntType

        where:
        lit    | type                               || res_val
        2      | int_t                              || 2
        2.3d   | double_t                           || 2
        9.9d   | double_t                           || 10
        3.1d   | new UnitType("a")                  || 3
        1.12d  | make_compound(["a", "c"],[-7,9])   || 1

    }

    def "Cast to double"(){
        given:
        def stack_val = new StackValue(new Literal<Object>(lit), type)

        when:
        def ret_val = casting.cast(stack_val, double_t)

        then:
        ret_val.getValue() instanceof Double
        Math.abs(((Double) ret_val.getValue())-lit) < Casting.EPSILON
        ret_val.getType() instanceof DoubleType

        where:
        lit  | type
        14   | int_t
        -9   | int_t
        1    | new UnitType("a")
        12   | make_compound(["o", "z"], [0,-8])
        0.9d | new UnitType("ssssss")
    }

    def "Check casting numeric to unit"(){
        given:
        def stack_val = new StackValue(new Literal<Object>(lit), type)

        expect:
        casting.cast(stack_val, ret_type).getType() == ret_type

        where:
        lit     | type     | ret_type
        14      | int_t    | new UnitType("a")
        -9      | int_t    | make_compound(["a", "b"], [0,9])
        1.0d    | double_t | new UnitType("z")
        -12.1d  | double_t | make_compound(["l", "o"], [8, 0])
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
        "k"     | string_t      || bool_t
        "a"     | string_t      || int_t
        "z"     | string_t      || double_t
        "k"     | string_t      || new UnitType("o")
        "k"     | string_t      || make_compound(["l", "o"], [1, 8])
        true    | bool_t        || int_t
        true    | bool_t        || double_t
        true    | bool_t        || new UnitType("s")
        true    | bool_t        || make_compound(["z"], [-2])
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

    def "Check calculating types for multiplication"(){
        when:
        def res = casting.calculateTypeForMultiplication(first, second)

        then:
        res == expected

        where:
        first    | second   || expected
        double_t | int_t    || double_t
        int_t    | double_t || double_t
        int_t    | int_t    || int_t
        string_t | int_t    || string_t
        int_t    | string_t || string_t
    }

    def "Check calculating unit types for multiplication"(){
        when:
        def res = casting.calculateTypeForMultiplication(first, second)

        then:
        res == expected

        where:
        first                                       | second                || expected
        new UnitType("a")                           | double_t              || new UnitType("a")
        new UnitType("a")                           | int_t                 || new UnitType("a")
        new UnitType("a")                           | new UnitType("b")     || make_compound(["a", "b"], [1,1])
        make_compound(["a", "b", "c"], [1,1,1])     | double_t              || first

        int_t                                       | make_compound(["a","b","c"], [1, 1, 1]) || second
        make_compound(["a", "b", "c"], [1,1,1])     | make_compound(["a","b","d"], [8,1,-3])  || make_compound(["a", "b", "c", "d"], [9,2,1,-3])
        make_compound(["a"], [-1])                  | new UnitType("a")                       || double_t
        make_compound(["a"], [2])                   | make_compound(["a"], [-1])              || new UnitType("a")
        double_t                                    | make_compound(["a", "b", "c"], [1,1,1]) || second
    }

    def static make_compound(ArrayList<String> terms, ArrayList<Integer> exponents){
        var expr = new CompoundExpr()
        var size = terms.size()
        for(int i=0; i<size; i++){
            expr.addPart(terms[i], exponents[i])
        }
        return new CompoundType(expr);
    }

    def "Check calculate type for multiplication errors"(){
        when:
        casting.calculateTypeForMultiplication(first, second)

        then:
        thrown(InterpretingException)

        where:
        first                       | second
        string_t                    | string_t
        make_compound(["a"], [2])   | string_t
        string_t                    | bool_t
        string_t                    | double_t
        bool_t                      | bool_t
        bool_t                      | int_t
        bool_t                      | new UnitType("a")
    }

    def "Check multiplyString"(){
        given:
        String str = "s-s";

        expect:
        casting.multiplyString(str, 3) == "s-ss-ss-s"
    }

    def "Check multiply with value cast for integers and string"(){
        expect:
        casting.multiplyWithValueCast(first, second) == res

        where:
        first   | second    || res
        "s-s"   | 3         || "s-ss-ss-s"
        4       | "a"       || "aaaa"
        3       | 12        || first * second
        0       | "d"       || ""
        1       | 0         || 0
        0.0d    | "d"       || ""
    }

    def "Check multiply with value cast for doubles"(){
        when:
        def result = casting.multiplyWithValueCast(first, second) as Double

        then:
        Math.abs(result - res) < casting.EPSILON

        where:
        first   | second    || res
        3       | 12.4d     || 3 * 12.4
        1.4d    | 0.5d      || 0.5d * 1.4d
        2       | 0.5d      || 0.5d * 2
        2.2d    | 4         || 2.2d * 4
    }

    def "Check multiply with value cast errors"(){
        when:
        casting.multiplyWithValueCast(first, second)

        then:
        thrown(InterpretingException)

        where:
        first   | second
        "a"     | 12.4d
        "s"     | "s"
        "s"     | true
        true    | false
        true    | 2
        true    | 2.3d
        "s"     | -7
    }

    def "Calculate type for division"(){
        expect:
        casting.calculateTypeForDivision(first, second) == res

        where:
        first       | second    || res
        int_t       | int_t     || double_t
        int_t       | double_t  || double_t
        double_t    | int_t     || double_t
        double_t    | double_t  || double_t
        // with units
        int_t                               | new UnitType("a")                 || second
        double_t                            | new UnitType("b")                 || second
        new UnitType("a")                   | new UnitType("b")                 || make_compound(["a","b"], [1,-1])
        make_compound(["a", "b"], [1,3])    | make_compound(["b", "c"], [2,-2]) || make_compound(["a","b","c"], [1,1,2])

    }

    def "Check errors for division casting"(){
        when:
        casting.calculateTypeForDivision(first, second)

        then:
        thrown(InterpretingException)

        where:
        first       | second
        string_t    | string_t
        string_t    | bool_t
        string_t    | double_t
        string_t    | new UnitType("k")
        string_t    | int_t
        bool_t      | int_t
        bool_t      | double_t
        bool_t      | new UnitType("l")
    }

    def "Check divide with value cast for doubles"(){
        when:
        def result = casting.divideWithValueCast(first, second) as Double

        then:
        Math.abs(result - first/second) < casting.EPSILON

        where:
        first   | second
        3       | 12.4d
        1.4d    | 0.5d
        2       | 0.5d
        2.2d    | 4
        2       | 3
    }
    def "Check multiply with value cast for integers"(){
        when:
        def res = casting.divideWithValueCast(4, 2)

        then:
        res instanceof  Integer
        res == 2
    }

    def "Check divide with value cast errors"(){
        when:
        casting.divideWithValueCast(first, second)

        then:
        thrown(InterpretingException)

        where:
        first   | second
        3       | 0
        -2      | 0.0d
        2f      | 12
    }

    def "Compare to with cast"(){
        given:
        var first_stack = new StackValue(new Literal<Object>(first), null);
        var second_stack = new StackValue(new Literal<Object>(second), null);

        expect:
        casting.compareToWithCast(first_stack, second_stack) == res

        where:
        first   | second        || res
        2       | 2             || first <=> second
        -3      | 12            || first <=> second
        9.4d    | 5             || first <=> second
        9d      | 9             || first <=> second
        "a"     | "b"           || first <=> second
        "c"     | "c"           || first <=> second
        2       | 2d            || first <=> second
        1       | 1.05d         || first <=> second
        2.3d    | 2.3d          || first <=> second
    }

    def "Check errors for comparison"(){
        given:
        var first_stack = new StackValue(new Literal<Object>(first), null);
        var second_stack = new StackValue(new Literal<Object>(second), null);

        when:
        casting.compareToWithCast(first_stack, second_stack)

        then:
        thrown(InterpretingException)

        where:
        first   | second
        "a"     | true
        "b"     | 1
        "c"     | 1.2d
        true    | 1
        false   | -0.12
    }

    def "Check subtract with value cast for integers"(){
        expect:
        casting.subtractionWithValueCast(first, second) == first - second

        where:
        first   | second
        3       | 4
        -3      | 3
    }

    def "Check subtract with value cast for double"(){
        when:
        def res = casting.subtractionWithValueCast(first, second) as Double

        then:
        Math.abs(res - (first - second)) < casting.EPSILON

        where:
        first   | second
        3       | 4.0d
        -3.0d   | 3
        3.4d    | 1.3d
    }

    def "Check add with value cast for integers"(){
        expect:
        casting.additionWithValueCast(first, second) == first + second

        where:
        first   | second
        12      | 4
        -9      | 3
    }


    def "Check add with value cast for double"(){
        when:
        def res = casting.additionWithValueCast(first, second) as Double

        then:
        Math.abs(res - (first + second)) < casting.EPSILON

        where:
        first   | second
        3       | 4.0d
        -3.0d   | 3
        3.4d    | 1.3d
    }

    def "Check addition with value cast errors"(){
        when:
        casting.additionWithValueCast(first, second)

        then:
        thrown(InterpretingException)

        where:
        first   | second
        3       | 0f
        -2      | "s"
        2f      | true
        false   | "k"
    }

    def "Calculate type for unit exponentiation"(){
        expect:
        casting.exponentiateUnitType(unit_t, exp)


        where:
        unit_t                                  | exp   | res_t
        new UnitType("a")                       | 2     | make_compound(["a"], [2])
        make_compound(["a","b","c"], [1,3,-2])  | -3    | make_compound(["a","b","c"], [-3,-9,6])
    }

    def "Check type exponentiaiton calculation"(){
        given:
        var base = new StackValue(new Literal<Object>(null), base_t);
        var exp = new StackValue(new Literal<Object>(exp_val), exp_t);

        expect:
        casting.calculateTypeForExponentiation(base, exp)  == res_t

        where:
        base_t              | exp_t     | exp_val   | res_t
        int_t               | int_t     | null      | int_t
        int_t               | double_t  | null      | double_t
        double_t            | int_t     | null      | double_t
        double_t            | double_t  | null      | double_t
        new UnitType("a")   | int_t     | 4         | make_compound(["a"], [4])

        make_compound(["r","g"], [1,3]) | int_t | 3 | make_compound(["r", "g"], [3,9])
    }

    def "Check type exponentiaiton calculation errors"(){
        given:
        var base = new StackValue(new Literal<Object>(null), base_t);
        var exp = new StackValue(new Literal<Object>(null), exp_t);

        when:
        casting.calculateTypeForExponentiation(base, exp)

        then:
        thrown(InterpretingException)

        where:
        base_t                      | exp_t
        make_compound(["a"], [4])   | double_t
        new UnitType("a")           | double_t
        double_t                    | new UnitType("a")
        double_t                    | make_compound(["a"], [4])
        int_t                       | new UnitType("a")
        int_t                       | make_compound(["a"], [4])
    }

    def "Check exponentiation with value cast"(){
        when:
        def res = casting.exponentiateWithValueCast(first, second) as Double

        then:
        Math.abs(res - Math.pow(first, second)) < casting.EPSILON

        where:
        first   | second
        3       | 4.0d
        -3.0d   | 3
        3.4d    | 1.3d
        4       | 0
        12      | -2
    }

    def "Check type exponentiaiton with value cast errors"(){
        when:
        casting.exponentiateWithValueCast(first, second)

        then:
        thrown(InterpretingException)

        where:
        first   | second
        1       | 8.7f
        3f      | 4
    }
}
