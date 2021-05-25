package interpreter.util;

import tree.type.Type;
import tree.value.Literal;

public class StackValue {
    private final Literal<?> value;
    private final Type type;

    public StackValue(Literal<?> value, Type type) {
        this.value = value;
        this.type = type;
    }

    public Literal<?> getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }
}
