package tree.function;

import tree.Node;
import tree.expression.Expression;

import java.util.ArrayList;
import java.util.List;

public class Arguments  implements Node {
    private final List<Expression> arguments = new ArrayList<>();

    public void addArgument (Expression expr){
        arguments.add(expr);
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
