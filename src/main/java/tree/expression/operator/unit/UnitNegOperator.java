package tree.expression.operator.unit;

import interpreter.Visitor;
import tree.Visitable;

public class UnitNegOperator extends UnitOperator implements Visitable {
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "-";
    }
}
