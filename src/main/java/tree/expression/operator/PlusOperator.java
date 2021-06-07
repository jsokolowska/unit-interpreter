package tree.expression.operator;

import interpreter.Visitor;

public class PlusOperator extends Operator{
    @Override
    public String toString() {
        return "+";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
