package util.tree.statement;

import util.tree.type.UnitType;

public class ExplainStatement extends Statement{
    private final UnitType type;

    private ExplainStatement (UnitType type){
        this.type = type;
    }

}
