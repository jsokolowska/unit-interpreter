package tree.unit;

import interpreter.Visitor;
import tree.Visitable;
import tree.type.UnitType;

import java.util.HashMap;
import java.util.Map;

public class CompoundExpr implements Visitable {
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

    public boolean contains (UnitType part){
        if (part == null) return false;
        for (Map.Entry<UnitType, Integer> entry : compoundParts.entrySet()){
            if(entry.getKey().equals(part)){
                return entry.getValue() == 1;
            }
        }
        return false;
    }

    public boolean contains(Map.Entry<UnitType, Integer> entry){
        if(entry == null) return false;
        Integer val = compoundParts.get(entry.getKey());
        if(val == null) return false;
        return val.equals(entry.getValue());
    }

    /** removes terms with exponents == 0*/
    public void simplify (){
        compoundParts.values().removeIf(exponent -> exponent == 0);
    }

    public int size(){
        return compoundParts.size();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CompoundExpr){
            CompoundExpr that = (CompoundExpr) obj;
            return equalToCompoundExpr(that);

        }else if(obj instanceof UnitType){
            UnitType unit = (UnitType) obj;
            return equalToBaseUnit(unit);
        }
        return false;
    }

    private boolean equalToCompoundExpr(CompoundExpr that){
        if(that == null) return false;

        this.simplify();
        that.simplify();
        if(this.size() != that.size())return false;

        for(Map.Entry<UnitType, Integer> entry: this.compoundParts.entrySet()){
            if(!that.contains(entry)) return false;
        }
        return true;
    }

    private boolean equalToBaseUnit(UnitType unit){
        if(unit == null) return false;
        if(this.size() != 1){
            this.simplify();
            if(this.size() != 1){
                return false;
            }
        }
        return this.contains(unit);
    }
}
