package interpreter.util;

import tree.Variable;
import tree.type.Type;
import tree.value.Literal;

public class StackValue {
    private final Literal<?> value;
    private final Type type;

    public StackValue(Literal<?> value, Type type) {
        this.value = value;
        this.type = type;
    }

    public StackValue(Variable variable){
        value = variable.getValue();
        type = variable.getType();
    }

    public Literal<?> getValueAsLiteral(){
        return value;
    }
    
    public Object getValue() {
        return value.getLiteralValue();
    }

    public Type getType() {
        return type;
    }
}
