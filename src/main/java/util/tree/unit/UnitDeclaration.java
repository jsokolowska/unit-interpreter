package util.tree.unit;

import util.tree.unit.compound.CompoundExpr;

public class UnitDeclaration {
    private String name;
    private CompoundExpr type;

    public UnitDeclaration(String name){
        this(name, null);
    }

    public UnitDeclaration(String name, CompoundExpr type){
        this.name = name;
        this.type = type;
    }

    public CompoundExpr getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
