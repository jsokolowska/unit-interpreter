package util.tree.statement;

import util.tree.function.Arguments;

public class CallStatement extends Statement{
    private final String identifier;
    private final Arguments arguments;

    public CallStatement(String id, Arguments arguments){
        identifier = id;
        this.arguments = arguments;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return identifier + "(" + arguments.toString() + ")";
    }
}
