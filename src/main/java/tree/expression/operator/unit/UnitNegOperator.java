package tree.expression.operator.unit;

import interpreter.Visitor;

public class UnitNegOperator extends UnitOperator{
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "-";
    }
}
