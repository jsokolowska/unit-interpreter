package tree.statement;

import interpreter.Visitor;

public class BreakStatement extends Statement{


    @Override
    public String toString() {
        return "break";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
