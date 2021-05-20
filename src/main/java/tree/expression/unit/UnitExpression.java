package tree.expression.unit;

import interpreter.Visitor;
import tree.Visitable;

public class UnitExpression implements Visitable {

    @Override
    public String toString() {
        return "u";
    }

    public int size(){
        return 1;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
