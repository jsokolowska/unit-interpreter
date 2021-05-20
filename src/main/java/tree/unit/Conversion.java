package tree.unit;

import tree.Node;
import tree.expression.unit.UnitExpression;
import tree.type.UnitType;

public class Conversion implements Node {
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
}
