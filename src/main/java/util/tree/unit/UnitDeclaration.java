package util.tree.unit;

import util.tree.Node;

public class UnitDeclaration implements Node {
    private final String name;
    private final CompoundExpr type;

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
