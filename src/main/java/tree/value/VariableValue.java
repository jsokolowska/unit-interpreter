package tree.value;

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
}
