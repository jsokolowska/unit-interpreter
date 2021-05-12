package util.tree.type;

import util.tree.unit.CompoundExpr;

import java.util.Arrays;

public class CompoundType extends UnitType {
    private CompoundExpr expr;

    public CompoundType(String name) {
        super(name);
    }

    public CompoundExpr getExpr() {
        return expr;
    }

}
