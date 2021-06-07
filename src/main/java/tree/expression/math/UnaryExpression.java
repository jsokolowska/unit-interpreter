package tree.expression.math;

import interpreter.Visitor;
import tree.expression.Expression;
import tree.expression.operator.Operator;

public class UnaryExpression extends Expression {
    private Expression expr;
    private Operator op;

    public void add (Expression expr, Operator op){
        this.expr = expr;
        this.op = op;
    }

    public Expression getExpr() {
        return expr;
    }

    public Operator getOp() {
        return op;
    }

    @Override
    public String toString() {
        if(expr == null) return "_";
        return "[" + op + expr + ']';
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
