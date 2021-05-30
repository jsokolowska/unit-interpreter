package interpreter.util;

import com.sun.jdi.DoubleType;
import tree.type.*;
import tree.unit.CompoundExpr;
import tree.unit.CompoundTerm;
import tree.value.Literal;
import util.exception.CastingException;
import util.exception.InterpretingException;

import java.util.Map;

public class Casting {
    private final Integer line;
    private static final double EPSILON = 0.00001d;

    public Casting(Integer line){
        this.line = line;
    }

    public  boolean castToBoolean (StackValue stackValue){
        var val = stackValue.getValue();
        if(val instanceof Boolean b){
            return b;
        }else if(val instanceof Integer i){
            return i>0;
        }else if(val instanceof Double d){
            return d>0;
        }else if(val instanceof Float f){
            return f>0;
        }
        throw new CastingException(line,stackValue.getType().toString(), "bool");
    }

    public int castToInt (StackValue stackValue){
        var val = stackValue.getValue();
        if(val instanceof Integer i){
            return i;
        }
        throw new CastingException(line, stackValue.getType().toString(), "integer");
    }

    public double castToDouble(StackValue stackValue){
        var val = stackValue.getValue();
        if(val instanceof Double d){
            return d;
        }else if(val instanceof Integer i){
            return Double.valueOf(i);
        }
        throw new CastingException(line, stackValue.getType().toString(), "double");
    }

    public Type calculateTypeForMultiplication(Type first, Type second){
        if(!isNumberType(first) || !isNumberType(second)){
            throw new InterpretingException("Cannot add " + first + " and " + second, line);
        }
        if(first instanceof IntType || first instanceof FloatType){
            return second;
        }else if(second instanceof IntType || second instanceof FloatType){
            return first;
        }
        UnitType u1 = (UnitType) first;
        UnitType u2 = (UnitType) second;
        return multiplyUnitTypes(u1, u2);
    }

    private Type multiplyUnitTypes(UnitType first, UnitType second){
        if(first instanceof CompoundType compound1 && second instanceof CompoundType compound2){
            Map<String, Integer> compoundParts = compound1.getCompoundTerms();
            for(var entry : compoundParts.entrySet()){
                compound2.add(entry.getKey(), entry.getValue());
            }
            return compound2;
        }
        if(first instanceof CompoundType compound){
            compound.add(second.getName(), 1);
            return compound;
        }
        if(second instanceof CompoundType compound){
            compound.add(first.getName(), 1);
            return compound;
        }
        CompoundExpr expr = new CompoundExpr();
        expr.addPart(first.getName(), 1);
        expr.addPart(second.getName(), 1);
        return new CompoundType("compound", expr);
    }

    private boolean isNumberType(Type t){
        return t instanceof NumericType || t instanceof UnitType;
    }

    public Type calculateTypeForDivision (Type numerator, Type denominator){
        if(!isNumberType(numerator) || !isNumberType(denominator)){
            throw new InterpretingException("Cannot add " + numerator + " and " + denominator, line);
        }

        if(numerator instanceof UnitType u1 && denominator instanceof UnitType u2){
            return multiplyUnitTypes(u1, reverse(u2));
        }
        if(numerator instanceof UnitType){
            return numerator;
        }
        if(denominator instanceof UnitType unitType){
            return reverse(unitType);
        }
        if(denominator instanceof FloatType || numerator instanceof FloatType){
            return new FloatType();
        }
        return new IntType();
    }

    private UnitType reverse(UnitType type){
        if(type instanceof CompoundType compound){
            compound.reverse();
            return compound;
        }
        CompoundExpr expr = new CompoundExpr();
        expr.addPart(new CompoundTerm(type, -1));
        return new CompoundType("compound", expr);
    }

    public static Type getMatchingType(Literal<?> literal){
        var value = literal.getLiteralValue();
        if(value instanceof Boolean){
            return new BoolType();
        }else if(value instanceof String){
            return new StringType();
        }else if(value instanceof Integer){
            return new IntType();
        }
        return new FloatType();
    }

    public StackValue cast (StackValue variable, Type to){
        Type from = variable.getType();

        if(from.equals(to)) {
            //no cast needed
            return variable;
        }

        var val_obj = variable.getValue();
        if(to instanceof UnitType utype){
            return castToUnit(val_obj, from, utype);
        }
        if(to instanceof IntType){
            return castToInt(val_obj, from);
        }
        if(to instanceof DoubleType){
            return castToDouble(val_obj, from);
        }
        if(to instanceof BoolType){
            return castToBoolean(val_obj, from);
        }
        throw new CastingException(line, from.prettyToString(), to.prettyToString());
    }

    private StackValue castToUnit(Object value, Type from, UnitType to){
        if (from instanceof NumericType){
            return new StackValue(new Literal<>(value), to);
        }
        throw new CastingException(line, from.prettyToString(), to.prettyToString());
    }

    private StackValue castToInt(Object value, Type from){
        if(value instanceof Integer){
            return new StackValue(new Literal<>(value), new IntType());
        }
        if(value instanceof Double dVal){
            Integer iVal = Math.toIntExact(Math.round(dVal));
            return new StackValue(new Literal<>(iVal), new IntType());
        }
        throw new CastingException(line, from.prettyToString(), "integer");
    }

    private StackValue castToDouble(Object value, Type from){
        if(value instanceof Integer iVal){
            return new StackValue(new Literal<>(Double.valueOf(iVal)), new FloatType());
        }
        if(value instanceof Double dVal){
            return new StackValue(new Literal<>(dVal), new FloatType());
        }
        throw new CastingException(line, from.prettyToString(), "float");
    }

    private StackValue castToBoolean(Object value, Type from){
        if(value instanceof Integer i){
            return new StackValue(new Literal<>(i>0), new BoolType());
        }
        if(value instanceof Double d){
            return new StackValue(new Literal<>(d>0), new BoolType());
        }
        throw new CastingException(line, from.prettyToString(), "boolean");
    }

    public int compareToWithBooleanCast(StackValue left, StackValue right){
        var rValue = right.getValue();
        var lValue = left.getValue();
        if(rValue instanceof Integer rNum && lValue instanceof Integer lNum){
            return lNum.compareTo(rNum);
        }else if(rValue instanceof Double rDouble && lValue instanceof Double lDouble){
            return lDouble.compareTo(rDouble);
        }else if(rValue instanceof Double rNum && lValue instanceof Integer lNum) {
            return - rNum.compareTo(Double.valueOf(lNum));
        }else if(rValue instanceof Integer rNum && lValue instanceof Double lNum) {
            return lNum.compareTo(Double.valueOf(rNum));
        }else {
            throw new InterpretingException("Cannot compare " + left.getType() + " and " + right.getType(), line);
        }
    }

    public Literal<?> subtractWithValueCast(Number lValue, Number rValue){
        Number resultVal = null;
        if(rValue instanceof Double rDouble){
            resultVal = doAddition(lValue, -rDouble);
        }else if(lValue instanceof Integer rInteger){
            resultVal = doAddition(lValue, -rInteger);
        }
        return new Literal<>(resultVal);
    }

    public Number doAddition(Number one, Number two){
        if(one instanceof Integer rInt && two instanceof  Integer lInt){
            return lInt + rInt;
        }else if(one instanceof Integer rInt && two instanceof  Double lDb){
            return lDb + rInt;
        }else if(one instanceof Double rDb && two instanceof  Double lDb){
            return lDb + rDb;
        }else if(one instanceof Double rDb && two instanceof  Integer lInt){
            return lInt + rDb;
        }
        throw new InterpretingException("Unrecognized value", line);
    }

    public Number divideWithValueCast(Number lValue, Number rValue){
        if (isZero(rValue)){
            throw new InterpretingException("Division by zero", line);
        }

        if(rValue instanceof Integer rInt && lValue instanceof  Integer lInt){
            if(lInt % rInt == 0){
                return lInt/rInt;
            }else{
                return Double.valueOf(lInt)/Double.valueOf(rInt);
            }
        }else if(rValue instanceof Integer rInt && lValue instanceof  Double lDb){
            return lDb/rInt;
        }else if(rValue instanceof Double rDb && lValue instanceof  Double lDb){
            return lDb/rDb;
        }else if(rValue instanceof Double rDb && lValue instanceof  Integer lInt){
            return lInt/rDb;
        }else{
            throw new InterpretingException("Unrecognized numeric value", line);
        }
    }
    
    public static boolean isZero(Number num){
        if(num instanceof Integer iNum){
            return iNum == 0;
        }else if (num instanceof Double dNum){
            return Math.abs(dNum) < EPSILON;
        }
        return false;
    }

    public Number multiplyWithValueCast(Number lValue, Number rValue){
        if (Casting.isZero(lValue) || Casting.isZero(rValue)){
            return 0;
        }else if(rValue instanceof Integer rInt && lValue instanceof  Integer lInt){
            return lInt * rInt;
        }else if(rValue instanceof Integer rInt && lValue instanceof  Double lDb){
            return lDb * rInt;
        }else if(rValue instanceof Double rDb && lValue instanceof  Double lDb){
            return lDb * rDb;
        }else if(rValue instanceof Double rDb && lValue instanceof  Integer lInt){
            return lInt * rDb;
        }else{
            throw new InterpretingException("Unrecognized value", line);
        }
    }

}
