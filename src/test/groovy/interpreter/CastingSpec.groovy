package interpreter

import interpreter.util.Casting
import interpreter.util.StackValue
import spock.lang.Specification
import tree.type.BoolType
import tree.type.CompoundType
import tree.type.FloatType
import tree.type.IntType
import tree.type.StringType
import tree.type.UnitType
import tree.unit.CompoundExpr
import tree.value.Literal
import util.exception.CastingException
import util.exception.InterpretingException

class CastingSpec extends Specification{

    Casting casting = new Casting(new Integer(2))

    def "Cast to boolean"(){
        expect:
        casting.castToBoolean(stackVal) ==  val

        where:
        stackVal                                                            || val
        new StackValue(new Literal<>(true), new BoolType())                 || true
        new StackValue(new Literal<>(false), new BoolType())                || false
        new StackValue(new Literal<>(2), new IntType())                     || true
        new StackValue(new Literal<>(-3), new IntType())                    || false
        new StackValue(new Literal<>(new Float(12.67)), new FloatType())    || true
        new StackValue(new Literal<>(new Float(-9.03)), new FloatType())    || false
    }

    def "Check casting exceptions for boolean"(){
        when:
        casting.castToBoolean(new StackValue(new Literal<String>("k"), new StringType()))

        then:
        thrown(CastingException)
    }

    def "Cast to integer"(){
        when:
        def value = casting.castToInt(new StackValue(new Literal<>(2), new IntType() ))

        then:
        value instanceof Integer
        value == 2
    }

    def "Check casting exceptions for integers"(){
        when:
        casting.castToInt(new StackValue(lit, new IntType()))

        then:
        thrown(CastingException)

        where:
        lit <<[new Literal<>(2.02f), new Literal<>("kkk")]
    }

    def "Cast to double"(){
        when:
        casting.castToDouble(new StackValue(lit, null))

        then:
        noExceptionThrown()

        where:
        lit <<[new Literal<>(new Double(12.9))]
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
        def res = casting.calculateTypeForDivision(new IntType(), new FloatType())

        then:
        res instanceof FloatType
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
}
