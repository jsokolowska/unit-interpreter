package tree.type;

import tree.unit.CompoundExpr;

import java.util.Map;

public class CompoundType extends UnitType {
    private final CompoundExpr expr;

    public CompoundType(String name, CompoundExpr expr) {
        super(name);
        this.expr = expr;
    }

    public CompoundExpr getExpr() {
        return expr;
    }

    public Map<String, Integer> getCompoundTerms(){
        return expr.getCompoundParts();
    }
    public void reverse(){
        expr.reverse();
    }

    public void add(String name, Integer value){
        expr.addPart(name, value);
    }

    public boolean contains(String name, int val){
        return expr.contains(name, val);
    }

    public int size(){
        return expr.size();
    }

    @Override
    public String toString() {
        return "[c]" + name;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getExpr().equals(obj);
    }

    @Override
    public String prettyToString(){
        return "Compound unit " + name;
    }
}
