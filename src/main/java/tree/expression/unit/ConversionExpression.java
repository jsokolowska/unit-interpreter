package tree.expression.unit;

import interpreter.Visitor;
import tree.expression.operator.UnitOperator;

import java.util.ArrayList;
import java.util.List;

public class ConversionExpression extends UnitExpression {

    private final List<UnitExpression> expressions = new ArrayList<>();
    private final List<UnitOperator> operators = new ArrayList<>();


    public void add(UnitExpression expr, UnitOperator operator){
        expressions.add(expr);
        operators.add(operator);
    }

    public void add(UnitExpression expr){
        expressions.add(expr);
    }

    public List<UnitExpression> getExpressions() {
        return expressions;
    }

    public List<UnitOperator> getOperators() {
        return operators;
    }

    @Override
    public String toString() {
        if(expressions.size() == 0) return "_";

        StringBuilder str = new StringBuilder();
        str.append(expressions.get(0));
        for(int i=1; i<expressions.size(); i++){
            str.append(operators.get(i-1));
            str.append(expressions.get(i));
        }
        return str.toString();
    }

    @Override
    public int size() {
        return expressions.size();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
