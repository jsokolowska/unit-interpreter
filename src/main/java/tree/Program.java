package tree;

import interpreter.Visitor;
import tree.function.Function;
import tree.unit.Conversion;
import tree.unit.UnitDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program implements Visitable {
    private final List<UnitDeclaration> unitDcls = new ArrayList<>();
    private final List<Conversion> conversions = new ArrayList<>();
    private final Map<String, Function> functions = new HashMap<>();

    public void add (UnitDeclaration unitDeclaration){
        unitDcls.add(unitDeclaration);
    }

    public void add (Conversion conversion){
        conversions.add(conversion);
    }

    public void add (Function function){
        functions.put(function.getIdentifier(), function);
    }

    public boolean hasFunctions (){
        return functions.size() > 0;
    }

    public boolean functionExists(String funName) {
        return functions.containsKey(funName);
    }

    public Function getFunction(String funName){
        return functions.get(funName);
    }

    public List<UnitDeclaration> getUnitDcls(){
        return unitDcls;
    }

    public List<Conversion> getConversions(){
        return conversions;
    }

    public Map<String, Function> getFunctions(){
        return functions;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
