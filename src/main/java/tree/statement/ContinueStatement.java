package tree.statement;

import interpreter.Visitor;

public class ContinueStatement extends Statement{

    @Override
    public String toString() {
        return "continue";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
