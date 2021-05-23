package tree.type;

import tree.unit.CompoundExpr;

public class CompoundType extends UnitType {
    private final CompoundExpr expr;

    public CompoundType(String name, CompoundExpr expr) {
        super(name);
        this.expr = expr;
    }

    public CompoundExpr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "[c]" + name;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getExpr().equals(obj);
    }

    @Override
    public String prettyToString(){
        return "Compound unit " + name;
    }
}
