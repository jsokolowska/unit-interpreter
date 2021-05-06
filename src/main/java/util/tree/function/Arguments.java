package util.tree.function;

import util.tree.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class Arguments {
    private final List<Expression> arguments = new ArrayList<>();

    public void addArguments (Expression expr){
        arguments.add(expr);
    }

    @Override
    public String toString() {
        var str = new StringBuilder();
        for (Expression e: arguments){
            str.append(e);
        }
        return str.toString();
    }
}
