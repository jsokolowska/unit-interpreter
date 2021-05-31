package tree.value;

import interpreter.Visitor;
import tree.Visitable;

public class VariableValue extends Value implements Visitable {
    protected final String identifier;

    public VariableValue(String identifier){
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }

    public String getIdentifier(){
        return identifier;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
