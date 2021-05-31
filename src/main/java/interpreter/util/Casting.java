package interpreter.util;

import tree.type.*;
import tree.unit.CompoundExpr;
import tree.unit.CompoundTerm;
import tree.value.Literal;
import util.exception.CastingException;
import util.exception.InterpretingException;

import java.util.Map;

public class Casting {
    private final Integer line;
    public static final double EPSILON = 0.00001d;

    public Casting(Integer line){
        this.line = line;
    }

    public StackValue cast (StackValue variable, Type to){
        Type from = variable.getType();

        if(from.equals(to)) {
            //no cast needed
            return variable;
        }

        var val_obj = variable.getValue();

        if(to instanceof StringType){
            return castToString(val_obj, from);
        }
        if(to instanceof BoolType){
            return castToBoolean(val_obj, from);
        }
        if(to instanceof IntType){
            return castToInt(val_obj, from);
        }
        if(to instanceof DoubleType){
            return castToDouble(val_obj, from);
        }
        if(to instanceof UnitType utype){
            return castToUnit(val_obj, from, utype);
        }

        throw new CastingException(line, from.prettyToString(), to.prettyToString());
    }

    private StackValue castToString(Object value, Type from){
        String val = String.valueOf(value);
        if(from instanceof UnitType u){
            return new StackValue(new Literal<>(val +" [" + u.prettyToString() + "]"), new StringType());
        }
        return new StackValue(new Literal<>(val), new StringType());
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
            return new StackValue(new Literal<>(Double.valueOf(iVal)), new DoubleType());
        }
        if(value instanceof Double dVal){
            return new StackValue(new Literal<>(dVal), new DoubleType());
        }
        throw new CastingException(line, from.prettyToString(), "float");
    }

    private StackValue castToUnit(Object value, Type from, UnitType to){
        if (from instanceof NumericType){
            return new StackValue(new Literal<>(value), to);
        }
        throw new CastingException(line, from.prettyToString(), to.prettyToString());
    }

    public Type calculateTypeForMultiplication(Type first, Type second){
        if(!isNumberType(first) || !isNumberType(second)){
            throw new InterpretingException("Cannot multiplicate " + first.prettyToString() + " and " + second.prettyToString(), line);
        }
        if(first instanceof UnitType u1 && second instanceof UnitType u2){
            return multiplyUnitTypes(u1, u2);
        }else if(first instanceof UnitType){
            return first;
        }else if(second instanceof UnitType){
            return second;
        }else if(first instanceof IntType && second instanceof IntType){
            return first;
        }else if(second instanceof DoubleType || first instanceof DoubleType){
            return new DoubleType();
        }else if(first instanceof StringType && second instanceof IntType){
            return first;
        }
        throw new InterpretingException("Cannot multiplicate " + first + " and " + second, line);
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

    public Object multiplyWithValueCast(Object lValue, Object rValue){
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
        }else if(lValue instanceof String str && rValue instanceof Integer int_v){
            return multiplyString(str, int_v);
        }else if(rValue instanceof String str && lValue instanceof Integer int_v){
            return multiplyString(str, int_v);
        }else{
            throw new InterpretingException("Unrecognized value", line);
        }
    }

    private String multiplyString(String str, int n){
        if(n < 0){
            throw new InterpretingException("Cannpt multiply string and int < 0" );
        }else{
            return str.repeat(n);
        }
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
        if(denominator instanceof DoubleType || numerator instanceof DoubleType){
            return new DoubleType();
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
        return new DoubleType();
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

    public Literal<?> subtractionWithValueCast(Number lValue, Number rValue){
        Number resultVal = null;
        if(rValue instanceof Double rDouble){
            resultVal = additionWithValueCast(lValue, -rDouble);
        }else if(lValue instanceof Integer rInteger){
            resultVal = additionWithValueCast(lValue, -rInteger);
        }
        return new Literal<>(resultVal);
    }

    public Number additionWithValueCast(Number one, Number two){
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
    
    public static boolean isZero(Object num){
        if(num instanceof Integer iNum){
            return iNum == 0;
        }else if (num instanceof Double dNum){
            return Math.abs(dNum) < EPSILON;
        }
        return false;
    }

    public Type calculateTypeForExponentiation(StackValue base, StackValue exponent){
        Type baseType = base.getType();
        Type expType = base.getType();
        if(!isNumberType(expType) || !isNumberType(baseType)) {
            throw new InterpretingException("Cannot exponentiate" + baseType.prettyToString() + expType.prettyToString());
        }else{
            if(baseType instanceof IntType && exponent.getValue() instanceof Integer){
                return new IntType();
            }
            if(baseType instanceof UnitType unitType && expType instanceof IntType){
                int exp = (int) exponent.getValue();
                return exponentiateUnitType(unitType, exp);
            }
            if(baseType instanceof UnitType){
                return baseType;
            }
            return new DoubleType();
        }
    }

    private CompoundType exponentiateUnitType(UnitType t, int exponent){
        if(t instanceof CompoundType compound){
            compound.exponentiate(exponent);
            return compound;
        }
        CompoundExpr expr = new CompoundExpr();
        expr.addPart(t.getName(), exponent);
        return new CompoundType(expr);
    }

    public Number exponentiateWithValueCast(Number lValue, Number rValue){
        if(lValue instanceof Integer lInt){
            if(rValue instanceof Integer rInt){
                return (int) Math.pow(lInt, rInt);
            }else if(rValue instanceof Double rDouble){
                return Math.pow(lInt, rDouble);
            }
        }else if(lValue instanceof Double lDouble){
            if(rValue instanceof Integer rInt){
                return (int) Math.pow(lDouble, rInt);
            }else if(rValue instanceof Double rDouble){
                return Math.pow(lDouble, rDouble);
            }
        }
        throw new InterpretingException("Unrecognized value", line);
    }

}
