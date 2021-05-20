package tree.expression.unit.value;

import tree.expression.unit.UnitExpression;

public class UnitExpressionVariableValue extends UnitExpression {
    private final String identifier;

    public UnitExpressionVariableValue(String identifier){
        this.identifier = identifier;
    }

}
