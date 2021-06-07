package tree.expression.math;

import interpreter.Visitor;
import tree.expression.Expression;
import tree.expression.operator.Operator;

import java.util.ArrayList;
import java.util.List;

public class ExpressionWithOperators extends Expression {
    protected final List<Expression> expressions = new ArrayList<>();
    protected final List<Operator> operators = new ArrayList<>();

    public void add (Expression expr){
        expressions.add(expr);
    }

    public void add (Expression expr, Operator op){
        operators.add(op);
        expressions.add(expr);
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public List<Operator> getOperators() {
        return operators;
    }

    @Override
    public String toString() {
        if(expressions.size() == 0) return "_";
        StringBuilder str = new StringBuilder();
        if (expressions.size() > 1) str.append('[');
        str.append(expressions.get(0));
        for(int i=1; i < expressions.size(); i++){
            str.append(operators.get(i-1));
            str.append(expressions.get(i));
        }
        if (expressions.size() > 1) str.append(']');

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
