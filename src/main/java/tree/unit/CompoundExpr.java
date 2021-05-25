package tree.unit;

import interpreter.Visitor;
import tree.Visitable;
import tree.type.UnitType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CompoundExpr implements Visitable {
    private final Map<String, Integer> compoundParts = new HashMap<>();


    public void addPart(CompoundTerm part){
        Integer presVal = compoundParts.get(part.getUnitName());
        if(presVal != null){
            var newVar = presVal +  part.getExponent();
            compoundParts.put(part.getUnitName(), newVar);
            return;
        }
        compoundParts.put(part.getUnitName(), part.getExponent());
    }

    public void addPart(String name, Integer val){
        Integer presVal = compoundParts.get(name);
        if(presVal != null){
            var newVar = presVal +  val;
            compoundParts.put(name, newVar);
            return;
        }
        compoundParts.put(name, val);
    }

    public boolean contains (UnitType part){
        return compoundParts.containsKey(part.getName());
    }

    public boolean contains(String key, Integer exp){
        if(key == null || exp == null) return false;
        Integer val = compoundParts.get(key);
        if(val == null) return false;
        return val.equals(exp);
    }

    public boolean contains(CompoundTerm term){
        if(term == null) return false;
        return compoundParts.containsKey(term.getUnitName());
    }

    public Map<String, Integer> getCompoundParts() {
        return compoundParts;
    }
    public void reverse(){
        compoundParts.replaceAll((k,v)-> -1 * v);
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
        if(obj instanceof CompoundExpr that){
            return equalToCompoundExpr(that);
        }else if(obj instanceof UnitType unit){
            return equalToBaseUnit(unit);
        }
        return false;
    }

    private boolean equalToCompoundExpr(CompoundExpr that){
        if(that == null) return false;

        this.simplify();
        that.simplify();
        if(this.size() != that.size())return false;

        for(Map.Entry<String, Integer> entry: this.compoundParts.entrySet()){
            if(!that.contains(entry.getKey(), entry.getValue())) return false;
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
