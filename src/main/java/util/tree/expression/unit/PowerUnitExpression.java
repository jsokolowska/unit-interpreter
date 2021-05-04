package util.tree.expression.unit;

import java.util.ArrayList;
import java.util.List;

public class PowerUnitExpression extends UnitExpression{
    private final List<UnitExpression> expressions = new ArrayList<>();

    public void add(UnitExpression expr){
        expressions.add(expr);
    }

    @Override
    public String toString() {
        if(expressions.size() == 0) return "_";
        StringBuilder str = new StringBuilder();
        if (expressions.size() > 1) str.append('[');
        str.append(expressions.get(0));
        for(int i=1; i<expressions.size(); i++){
            str.append('^');
            str.append(expressions.get(i));
        }
        if (expressions.size() > 1) str.append(']');
        return str.toString();
    }
}
