package tree.value;

import interpreter.Visitor;
import tree.Visitable;

public class Literal<T> extends Value implements Visitable {
    T value;
    public Literal (T value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
