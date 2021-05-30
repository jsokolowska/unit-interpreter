package tree.expression.unit;

import interpreter.Visitor;

import java.util.ArrayList;
import java.util.List;

public class PowerUnitExpression extends UnitExpression {

    public void add(UnitExpression expr){
        expressions.add(expr);
    }

    @Override
    public String toString() {
        if(expressions.size() == 0) return "_";
        StringBuilder str = new StringBuilder();
        if (expressions.size() > 1) str.append('[');
        str.append(expressions.get(0));
        for(int i=1; i<expressions.size(); i++){
            str.append('^');
            str.append(expressions.get(i));
        }
        if (expressions.size() > 1) str.append(']');
        return str.toString();
    }

    @Override
    public int size() {
        return expressions.size();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
