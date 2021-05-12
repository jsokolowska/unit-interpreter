package util.tree.expression.math;

import util.tree.expression.Expression;
import util.tree.expression.operator.Operator;

public class UnaryExpression extends Expression {
    private Expression expr;
    private Operator op;

    public void add (Expression expr, Operator op){
        this.expr = expr;
        this.op = op;
    }

    @Override
    public String toString() {
        if(expr == null) return "_";
        return "[" + op + expr + ']';
    }
}
