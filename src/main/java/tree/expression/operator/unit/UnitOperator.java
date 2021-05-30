package tree.expression.operator.unit;

import interpreter.Visitor;
import tree.Visitable;
import tree.expression.operator.Operator;

public abstract class UnitOperator extends Operator implements Visitable {

    @Override
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
