package util.tree.var;

import util.tree.unit.compound.CompoundExpr;

public class CompoundVariable {
    private double value;
    private CompoundExpr type;
    public CompoundVariable(CompoundExpr type, double value){
        this.value = value;
        this.type = type;
    }
}
