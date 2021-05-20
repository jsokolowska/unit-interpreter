package tree.expression.math;

import interpreter.Visitor;
import tree.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class OrExpression extends Expression {
    private final List<Expression> expressions = new ArrayList<>();

    public void add (Expression e){
        expressions.add(e);
    }

    @Override
    public String toString() {
        if(expressions.size() == 0) return "_";
        StringBuilder str = new StringBuilder();
        str.append(expressions.get(0));

        for(int i=1; i < expressions.size(); i++){
            str.append("||");
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
