package util.tree.expression.unit;


import util.tree.expression.operator.Operator;

import java.util.ArrayList;
import java.util.List;

public class MulUnitExpression extends UnitExpression{
    private final List<UnitExpression> expressions = new ArrayList<>();
    private final List<Operator> operators = new ArrayList<>();


    public void add(UnitExpression expr, Operator operator){
        expressions.add(expr);
        operators.add(operator);
    }

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
            str.append(operators.get(i-1));
            str.append(expressions.get(i));
        }
        if (expressions.size() > 1) str.append(']');
        return str.toString();
    }
}
