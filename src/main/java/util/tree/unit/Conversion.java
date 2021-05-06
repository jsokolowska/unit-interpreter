package util.tree.unit;

import util.tree.Node;
import util.tree.expression.unit.ConversionExpression;
import util.tree.function.Parameters;
import util.tree.type.UnitType;

public class Conversion implements Node {
    private final UnitType to;
    private final Parameters parameters;
    private final ConversionExpression conversionExpression;

    public Conversion (UnitType to, Parameters parameters, ConversionExpression conversionExpression){
        this.conversionExpression = conversionExpression;
        this.to = to;
        this.parameters = parameters;
    }
}
