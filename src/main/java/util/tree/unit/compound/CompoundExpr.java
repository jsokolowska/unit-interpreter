package util.tree.unit.compound;

import java.util.SortedSet;
import java.util.TreeSet;

public class CompoundExpr {
    private final SortedSet<CompoundPart> parts =  new TreeSet<>();

    public boolean addPart(CompoundPart part){
        return parts.add(part);
    }
}
