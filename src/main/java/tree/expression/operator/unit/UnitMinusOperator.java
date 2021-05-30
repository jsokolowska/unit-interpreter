package tree.expression.operator.unit;

import interpreter.Visitor;

public class UnitMinusOperator extends UnitOperator{
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
    @Override
    public String toString() {
        return "-";
    }
}
