package util.tree.function;

import util.tree.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class Arguments {
    private final List<Expression> arguments = new ArrayList<>();

    public void addArguments (Expression expr){
        arguments.add(expr);
    }

}
