package tree.statement;

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
}
