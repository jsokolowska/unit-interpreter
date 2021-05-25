package tree.expression.operator;

import interpreter.Interpreter;
import interpreter.Visitor;

public class DivOperator extends UnitOperator{

    @Override
    public String toString() {
        return "/";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
