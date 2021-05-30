package tree.expression.unit;


import interpreter.Visitor;
import tree.expression.operator.unit.UnitOperator;

import java.util.ArrayList;
import java.util.List;

public class MulUnitExpression extends UnitExpression {
    private final List<UnitOperator> operators = new ArrayList<>();

    public void add(UnitExpression expr, UnitOperator operator){
        expressions.add(expr);
        operators.add(operator);
    }

    public List<UnitOperator> getOperators() {
        return operators;
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

    @Override
    public int size (){
        return expressions.size();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
