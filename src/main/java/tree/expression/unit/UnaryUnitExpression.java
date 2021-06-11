package tree.expression.unit;

import interpreter.Visitor;

public class UnaryUnitExpression extends UnitExpression{
    private UnitExpression expr;

    public void add (UnitExpression expr){
        this.expr = expr;
    }

    public UnitExpression getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "[-" + expr + "]";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
