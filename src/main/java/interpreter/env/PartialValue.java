package interpreter.env;

import tree.type.Type;
import tree.value.Literal;

public class PartialValue {
    private Literal<?> literal;
    private Type type;

    public PartialValue(Literal<?> literal, Type type){
        this.literal = literal;
        this.type = type;
    }

    public Literal<?> getLiteral() {
        return literal;
    }

    public Type getType() {
        return type;
    }
}
