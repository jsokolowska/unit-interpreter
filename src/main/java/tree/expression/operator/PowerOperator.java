package tree.expression.operator;

import interpreter.Visitor;

public class PowerOperator extends UnitOperator{
    @Override
    public String toString() {
        return "^";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
