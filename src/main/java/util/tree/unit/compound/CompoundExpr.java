package util.tree.unit.compound;

import java.util.SortedSet;
import java.util.TreeSet;

public class CompoundExpr {
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
}
