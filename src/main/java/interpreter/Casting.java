package interpreter;

import com.sun.jdi.DoubleType;
import interpreter.util.StackValue;
import tree.type.*;
import tree.value.Literal;
import util.exception.CastingException;

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
        }
        throw new CastingException(line,stackValue.getType().toString(), "bool");
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
