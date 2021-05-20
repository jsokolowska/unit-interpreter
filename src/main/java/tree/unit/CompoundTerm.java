package tree.unit;

import tree.Node;
import tree.type.UnitType;

public class CompoundTerm implements Comparable<CompoundTerm>, Node {
    private final UnitType unit;
    private int exponent;

    public CompoundTerm (UnitType unit, int exponent){
        this.unit = unit;
        this.exponent = exponent;
    }
    public UnitType getUnitType() {
        return unit;
    }

    public int getExponent(){
        return  exponent;
    }

    public void negate(){
        exponent *= -1;
    }

    @Override
    public int compareTo (CompoundTerm c){
        return unit.getName().compareTo(c.getUnitType().getName());
    }

    @Override
    public boolean equals (Object obj){
        if(obj.getClass() != this.getClass()){
            return false;
        }
        CompoundTerm term = (CompoundTerm) obj;
        return term.getExponent() == this.getExponent() &&
                term.getUnitType() == this.getUnitType();
    }
}
