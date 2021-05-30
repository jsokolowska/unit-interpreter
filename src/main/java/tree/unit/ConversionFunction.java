package tree.unit;

import interpreter.Visitor;
import tree.AbstractFunction;
import tree.Visitable;
import tree.expression.unit.UnitExpression;
import tree.type.UnitType;

public class ConversionFunction extends AbstractFunction implements Visitable {
    private final UnitType to;
    private final UnitExpression conversionExpression;

    public ConversionFunction(UnitType to, UnitParameters parameters, UnitExpression conversionExpression){
        this.conversionExpression = conversionExpression;
        this.to = to;
        this.params = parameters;
    }

    public UnitExpression getConversionExpression() {
        return conversionExpression;
    }

    public UnitType getResultType() {
        return to;
    }

    public UnitParameters getParameters() {
        return (UnitParameters) params;
    }

    public String getName(){
        return to.getName();
    }

    @Override
    public String toString() {
        return to.getName()+":"+params+"->{"+conversionExpression + "}";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
