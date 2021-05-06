package util.tree.expression.unit;


import util.tree.expression.Expression;
import util.tree.expression.operator.Operator;

import java.util.ArrayList;
import java.util.List;

public class MulUnitExpression extends Expression {
    private final List<Expression> expressions = new ArrayList<>();
    private final List<Operator> operators = new ArrayList<>();


    public void add(Expression expr, Operator operator){
        expressions.add(expr);
        operators.add(operator);
    }

    public void add(Expression expr){
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
}
