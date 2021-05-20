package tree.statement;

import interpreter.Visitor;
import tree.type.UnitType;

public class ExplainStatement extends Statement{
    private final UnitType type;

    public ExplainStatement(UnitType type){
        this.type = type;
    }

    @Override
    public String toString() {
        return "explain(" + type + ")";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
