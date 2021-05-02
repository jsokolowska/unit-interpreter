package util.tree;

import util.tree.function.Function;
import util.tree.unit.Conversion;
import util.tree.unit.Unit;
import util.tree.unit.UnitDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program {
    private final List<UnitDeclaration> unitDcls = new ArrayList<>();
    private final List<Conversion> conversions = new ArrayList<>();
    private final Map<String, Function> functions = new HashMap<>();

    // Enable Id lookup for units
    // private final Map<int, UnitDeclaration> unitDeclarationMap = new HashMap<>();
    // private final Map<int, Conversion> conversionMap = new HashMap<>();




    public void add (UnitDeclaration unitDeclaration){
        unitDcls.add(unitDeclaration);
    }

    public void add (Conversion conversion){
        conversions.add(conversion);
    }

    public void add (Function function){
        functions.put(function.getName(), function);
    }

    public boolean hasFunctions (){
        return functions.size() > 0;
    }
}
