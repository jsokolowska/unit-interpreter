package util.tree.unit;

import util.tree.Node;
import util.tree.expression.Expression;
import util.tree.function.Parameters;
import util.tree.type.UnitType;

public class Conversion implements Node {
    private final UnitType to;
    private final Parameters parameters;
    private final Expression conversionExpression;

    public Conversion (UnitType to, Parameters parameters, Expression conversionExpression){
        this.conversionExpression = conversionExpression;
        this.to = to;
        this.parameters = parameters;
    }
}
