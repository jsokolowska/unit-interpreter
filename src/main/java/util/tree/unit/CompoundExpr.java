package util.tree.unit;

import util.tree.Node;
import util.tree.type.UnitType;

import java.util.SortedSet;
import java.util.TreeSet;

public class CompoundExpr implements Node {
    private final SortedSet<CompoundTerm> parts =  new TreeSet<>();

    public boolean addPart(CompoundTerm part){
        return parts.add(part);
    }

    public boolean contains (CompoundTerm part){
        return  parts.contains(part);
    }

    public boolean hasTerms (){
        return parts.size()>0;
    }

    /** removes terms with exponents == 0*/
    public void simplify (){
        parts.removeIf(term -> term.getExponent() == 0);
    }

    /** Simplifies compound expression and checks if it corresponds to any base unit type
     * @return Corresponding Unit or null if expression can't be simplified to single unit*/
    public UnitType toSimpleUnit (){
        simplify();
        if(parts.size() != 1) return null; // can't be simplified
        for (CompoundTerm c : parts){
            return c.getUnitType();     //no better idea than that
        }
        return null;    //code unreachable but necessary
    }
}
