package util.tree.unit.compound;

import util.tree.unit.Unit;

public class CompoundPart implements Comparable<CompoundPart>{
    private Unit unit;
    private int exponent;

    public Unit getUnit() {
        return unit;
    }

    @Override
    public int compareTo (CompoundPart c){
        return unit.getName().compareTo(c.getUnit().getName());
    }
}
