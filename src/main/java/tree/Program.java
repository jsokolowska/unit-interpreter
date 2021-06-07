package tree;

import interpreter.Visitor;
import tree.unit.ConversionFunction;
import tree.unit.UnitDeclaration;

import java.util.HashMap;
import java.util.Map;

public class Program implements Visitable {
    private final Map<String, AbstractFunction> functions = new HashMap<>();
    private final Map<String, UnitDeclaration> unitDcls = new HashMap<>();

    public void add (AbstractFunction function){
        functions.put( function.getSignature(), function);
    }

    public void add (UnitDeclaration unitDeclaration){
        unitDcls.put(unitDeclaration.getUnitName(), unitDeclaration);
    }

    public boolean hasFunctions (){
        return functions.size() > 0;
    }

    public boolean functionExist(String signature){
        return functions.containsKey(signature);
    }

    public AbstractFunction getFunctionOrConversionFunction(String funKey){
        return functions.get(funKey);
    }


    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
