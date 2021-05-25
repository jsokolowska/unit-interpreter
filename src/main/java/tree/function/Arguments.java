package tree.function;

import interpreter.Visitor;
import tree.Visitable;
import tree.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class Arguments  {
    private final List<Expression> arguments = new ArrayList<>();

    public void addArgument (Expression expr){
        arguments.add(expr);
    }

    public List<Expression> getArgList() {
        return arguments;
    }

    public int size(){
        return arguments.size();
    }

    @Override
    public String toString() {
        if (arguments.size() == 0) return "none";
        var str = new StringBuilder();
        str.append(arguments.get(0));
        for (int i=1; i<arguments.size(); i++){
            str.append(", ");
            str.append(arguments.get(i));
        }
        return str.toString();
    }
}
