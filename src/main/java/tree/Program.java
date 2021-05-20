package tree;

import tree.function.Function;
import tree.unit.Conversion;
import tree.unit.UnitDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program implements Node{
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


}
