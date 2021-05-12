package util.tree.expression.math;

import util.tree.expression.Expression;
import util.tree.expression.operator.Operator;

import java.util.ArrayList;
import java.util.List;

public class RelationalExpression extends Expression {
    private final List<Expression> expressions = new ArrayList<>();
    private final List<Operator> relOperators = new ArrayList<>();

    public void add(Expression expr){
        expressions.add(expr);
    }

    public void add(Expression expr, Operator relOp){
        expressions.add(expr);
        relOperators.add(relOp);
    }

    @Override
    public String toString() {
        if(expressions.size() == 0) return "_";
        StringBuilder str = new StringBuilder();
        if (expressions.size() > 1) str.append('[');
        str.append(expressions.get(0));
        for(int i=1; i < expressions.size(); i++){
            str.append(expressions.get(i));
            str.append(relOperators.get(i-1));
        }
        if (expressions.size() > 1) str.append(']');

        return str.toString();
    }
}
