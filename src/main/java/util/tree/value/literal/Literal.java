package util.tree.value.literal;

import util.tree.value.Value;

public abstract class Literal extends Value {
    protected final Object value;

    public Literal(Object value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
