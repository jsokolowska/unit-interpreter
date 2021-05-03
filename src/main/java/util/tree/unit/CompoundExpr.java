package util.tree.unit;

import util.tree.Node;

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
}
