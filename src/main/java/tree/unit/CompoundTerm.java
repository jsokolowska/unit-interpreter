package tree.unit;

import interpreter.Visitor;
import tree.Visitable;
import tree.type.UnitType;

public class CompoundTerm implements Visitable {
    private final String unitName;
    private int exponent;

    public CompoundTerm (UnitType unit, int exponent){
        this.unitName = unit.getName();
        this.exponent = exponent;
    }

    public String getUnitName() {
        return unitName;
    }

    public int getExponent(){
        return  exponent;
    }

    public void negate(){
        exponent *= -1;
    }

    @Override
    public boolean equals (Object obj){
        if(! (obj instanceof CompoundTerm term)){
            return false;
        }
        return term.getExponent() == this.getExponent() &&
                term.getUnitName().equals(this.getUnitName());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
