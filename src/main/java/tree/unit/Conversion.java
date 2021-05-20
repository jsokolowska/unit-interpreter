package tree.unit;

import interpreter.Visitor;
import tree.Visitable;
import tree.expression.unit.UnitExpression;
import tree.type.UnitType;

public class Conversion implements Visitable {
    private final UnitType to;
    private final UnitParameters parameters;
    private final UnitExpression conversionExpression;

    public Conversion (UnitType to, UnitParameters parameters, UnitExpression conversionExpression){
        this.conversionExpression = conversionExpression;
        this.to = to;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return to.getName()+":"+parameters+"->{"+conversionExpression + "}";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
