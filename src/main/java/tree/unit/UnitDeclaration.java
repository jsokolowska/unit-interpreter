package tree.unit;

import interpreter.Visitor;
import tree.Visitable;

public class UnitDeclaration implements Visitable {
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

    public String getUnitName() {
        return name;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
