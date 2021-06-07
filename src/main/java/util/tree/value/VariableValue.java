package util.tree.value;

public class VariableValue extends Value{
    protected final String identifier;

    public VariableValue(String identifier){
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
