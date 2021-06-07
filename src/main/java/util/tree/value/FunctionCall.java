package util.tree.value;

import util.tree.function.Arguments;

public class FunctionCall extends Value {
    private final String identifier;    // can be id or unit type id
    private final Arguments args;

    public FunctionCall(String identifier, Arguments args){
        this.args = args;
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        if (args == null) return identifier + "()";
        return identifier + "(" + args+ ")";
    }
}
