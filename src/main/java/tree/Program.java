package tree;

import interpreter.Visitor;
import tree.function.Function;
import tree.unit.ConversionFunction;
import tree.unit.UnitDeclaration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program implements Visitable {
    private final Map<String, UnitDeclaration> unitDcls = new HashMap<>();
    private final Map<String, ConversionFunction> conversions = new HashMap<>();
    private final Map<String, Function> functions = new HashMap<>();


    public void add (UnitDeclaration unitDeclaration){
        unitDcls.put(unitDeclaration.getUnitName(), unitDeclaration);

    }

    public void add (ConversionFunction conversionFunction){
        conversions.put(conversionFunction.getName(), conversionFunction);
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

    public AbstractFunction getFunctionOrConversionFunction(String funName){
        AbstractFunction fun = functions.get(funName);
        if(fun!=null) return fun;

        return conversions.get(funName);
    }

    public Function getFunction(String funName){
        return functions.get(funName);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
