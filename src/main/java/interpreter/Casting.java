package interpreter;

import interpreter.util.StackValue;
import tree.type.*;
import tree.unit.CompoundExpr;
import tree.unit.CompoundTerm;
import tree.value.Literal;
import util.exception.CastingException;
import util.exception.InterpretingException;

import java.util.Map;

public class Casting {
    private final Integer line;

    public Casting(Integer line){
        this.line = line;
    }

    public  boolean castToBoolean (StackValue stackValue){
        var val = stackValue.getValue().getLiteralValue();
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
        var val = stackValue.getValue().getLiteralValue();
        if(val instanceof Integer i){
            return i;
        }
        throw new CastingException(line, stackValue.getType().toString(), "integer");
    }

    public double castToDouble(StackValue stackValue){
        var val = stackValue.getValue().getLiteralValue();
        if(val instanceof Float f){
            return f;
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
        return t instanceof IntType || t instanceof FloatType || t instanceof UnitType;
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

}
