package tree.expression.unit.value;

import tree.expression.unit.UnitExpression;

public class UnitExpressionLiteral <T> extends UnitExpression {
    T value;
    public UnitExpressionLiteral ( T value ){
        this.value = value;
    }

    public T getValue(){
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
