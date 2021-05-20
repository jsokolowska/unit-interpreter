package tree.unit;

import interpreter.Visitor;
import tree.Visitable;
import tree.type.Type;
import tree.type.UnitType;

import java.util.LinkedHashMap;
import java.util.Map;

public class UnitParameters implements Visitable {
    private final Map<String, UnitType> parameters = new LinkedHashMap<>();

    public void addParameter (String identifier, UnitType type){
        parameters.put(identifier, type);
    }

    public boolean contains (String identifier, UnitType type){
        Type result;
        if((result = parameters.get(identifier) )!= null){
            return result == type;
        }
        return false;
    }

    @Override
    public String toString() {
        if(parameters.size()==0) return "(none)";
        StringBuilder str = new StringBuilder("(");
        for (Map.Entry<String, UnitType> entry : parameters.entrySet()){
            str.append(entry.getValue());
            str.append(":");
            str.append(entry.getKey());
            str.append(",");
        }
        return str.substring(0, str.length()-1) + ")";

    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
