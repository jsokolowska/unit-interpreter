package tree.type;

import tree.unit.CompoundExpr;

import java.util.Map;

public class CompoundType extends UnitType {
    private final CompoundExpr expr;

    public CompoundType(String name, CompoundExpr expr) {
        super(name);
        this.expr = expr;
    }

    public CompoundType(CompoundType t){
        super("");
        this.expr = new CompoundExpr(t.getExpr());
    }

    public CompoundType(CompoundExpr expr) {
        super(null);
        this.expr = expr;
    }

    public CompoundType(UnitType t) {
        super(null);
        this.expr = new CompoundExpr();
        expr.addPart(t.getName(), 1);
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
        expr.simplify();
        return expr.size();
    }

    public void exponentiate(int exponent){
        expr.exponentiate(exponent);
    }

    @Override
    public String toString() {
        return "[c]" + name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        return this.getExpr().equals(obj);
    }

    @Override
    public String prettyToString(){
        return "compound " + expr.toString();
    }
}
