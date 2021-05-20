package tree;

import tree.expression.Expression;
import tree.type.Type;

public class Variable extends Expression implements Node{
    private final Type type;
    private final String identifier;

    public Variable (Type type, String identifier){
        this.type = type;
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return type.toString() +":" + identifier;
    }
}
