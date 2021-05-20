package tree.expression.unit;

public class UnaryUnitExpression extends UnitExpression{
    private UnitExpression expr;

    public void add (UnitExpression expr){
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "[-" + expr + "]";
    }

}
