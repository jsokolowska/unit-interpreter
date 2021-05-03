package util.tree.unit;

import util.tree.Node;

public class CompoundTerm implements Comparable<CompoundTerm>, Node {
    private Unit unit;
    private int exponent;

    public CompoundTerm (Unit unit, int exponent){
        this.unit = unit;
        this.exponent = exponent;
    }
    public Unit getUnit() {
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
        return unit.getName().compareTo(c.getUnit().getName());
    }

    @Override
    public boolean equals (Object obj){
        if(obj.getClass() != this.getClass()){
            return false;
        }
        CompoundTerm term = (CompoundTerm) obj;
        return term.getExponent() == this.getExponent() &&
                term.getUnit().getName().equals(this.getUnit().getName());
    }
}
