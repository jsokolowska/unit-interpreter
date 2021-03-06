package tree.value;

import interpreter.Visitor;
import tree.Visitable;

public class Literal<T> extends Value implements Visitable {
    T value;
    public Literal (T value){
        this.value = value;
    }

    public T getLiteralValue(){
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object that){ if (that instanceof Literal<?> l){
            return l.getLiteralValue().equals(value);
        }
        return false;
    }

}
