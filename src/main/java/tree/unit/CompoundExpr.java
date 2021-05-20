package tree.unit;

import tree.Node;
import tree.type.UnitType;

import java.util.HashMap;
import java.util.Map;

public class CompoundExpr implements Node {
    private final Map<UnitType, Integer> compoundParts = new HashMap<>();


    public void addPart(CompoundTerm part){
        Integer presVal = compoundParts.get(part.getUnitType());
        if(presVal != null){
            var newVar = presVal +  part.getExponent();
            compoundParts.put(part.getUnitType(), newVar);
            return;
        }
        compoundParts.put(part.getUnitType(), part.getExponent());
    }

    public boolean contains (CompoundTerm part){
        if (part == null) return false;
        UnitType type = part.getUnitType();
        for (Map.Entry<UnitType, Integer> entry : compoundParts.entrySet()){
            if(entry.getKey().equals(type)){
                return entry.getValue() == part.getExponent();
            }
        }
        return false;
    }

    public boolean hasTerms (){
        return compoundParts.size()>0;
    }

    /** removes terms with exponents == 0*/
    public void simplify (){
        compoundParts.values().removeIf(exponent -> exponent == 0);
    }

    /** Simplifies compound expression and checks if it corresponds to any base unit type
     * @return Corresponding Unit or null if expression can't be simplified to single unit*/
    public UnitType toSimpleUnit (){
        simplify();
        for (UnitType u : compoundParts.keySet()){
            return u;
        }
        return null;
    }

    public int size(){
        return compoundParts.size();
    }

}
