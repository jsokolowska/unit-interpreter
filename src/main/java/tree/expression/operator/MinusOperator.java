package tree.expression.operator;

import interpreter.Interpreter;
import interpreter.Visitor;

public class MinusOperator extends Operator{

    @Override
    public String toString() {
        return "-";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
