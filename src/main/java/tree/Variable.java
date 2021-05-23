package tree;

import interpreter.Visitor;
import tree.expression.Expression;
import tree.type.Type;
import tree.value.Literal;

public class Variable extends Expression implements Visitable {
    private Type type;
    private final String identifier;
    private Literal<?>  value = null;


    public Variable (Type type, String identifier){
        this.type = type;
        this.identifier = identifier;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setValue(Literal<?> value) {
        this.value = value;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return type.toString() +":" + identifier;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
