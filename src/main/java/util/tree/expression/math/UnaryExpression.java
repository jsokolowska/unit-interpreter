package util.tree.expression.math;

import util.tree.expression.Expression;

public class UnaryExpression extends Expression {
    private Expression expr;

    public void add (Expression expr){
        this.expr = expr;
    }

    @Override
    public String toString() {
        if(expr == null) return "_";
        return "-[" + expr + ']';
    }
}
