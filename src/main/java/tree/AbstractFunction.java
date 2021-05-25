package tree;

import interpreter.Visitor;
import tree.function.Parameters;

public abstract class AbstractFunction {
    protected Parameters params;

    public Parameters getParams() {
        return params;
    }
    public abstract void accept(Visitor visitor);
}
