package tree.expression.operator;

import interpreter.Visitor;
import tree.Visitable;

public class LessOperator extends Operator implements Visitable {

    @Override
    public String toString() {
        return "<";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
