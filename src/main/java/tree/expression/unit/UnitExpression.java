package tree.expression.unit;

import interpreter.Visitor;
import tree.Visitable;

import java.util.ArrayList;
import java.util.List;

public class UnitExpression implements Visitable {

    protected final List<UnitExpression> expressions = new ArrayList<>();

    public List<UnitExpression> getExpressions() {
        return expressions;
    }

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
