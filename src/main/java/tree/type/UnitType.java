package tree.type;

import tree.unit.CompoundExpr;

public class UnitType extends Type {
    protected final String name;
    protected UnitType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals (Object obj){
        if(obj instanceof CompoundType comp){
            CompoundExpr expr = comp.getExpr();
            return expr.equals(this);
        }else if(obj instanceof UnitType){
            return ((UnitType) obj).getName().equals(this.getName());
        }else if(obj instanceof CompoundExpr expr){
            return expr.equals(this);
        }
        return false;
    }

    @Override
    public String toString() {
        return "[u]" + name;
    }

}
