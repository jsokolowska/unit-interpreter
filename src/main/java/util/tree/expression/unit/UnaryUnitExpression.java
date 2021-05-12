package util.tree.expression.unit;

import util.tree.expression.Expression;

public class UnaryUnitExpression extends Expression{
    private Expression expr;

    public void add (Expression expr){
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "-[" + expr + "]";
    }

}
