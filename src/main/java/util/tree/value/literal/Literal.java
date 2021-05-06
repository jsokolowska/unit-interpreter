package util.tree.value.literal;

import util.tree.value.Value;

public class Literal extends Value {
    private final Object value;

    public Literal(Object value){
        this.value = value;
    }
}
