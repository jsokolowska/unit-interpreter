package tree.expression.unit.value;

import interpreter.Visitor;
import tree.expression.unit.UnitExpression;

public class UnitExpressionVariableValue extends UnitExpression {
    private final String identifier;

    public UnitExpressionVariableValue(String identifier){
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
