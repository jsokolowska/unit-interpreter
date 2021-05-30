package tree.unit;

import tree.function.Parameters;
import tree.type.UnitType;


//todo is it needed?
public class UnitParameters extends Parameters {
    public void addParameter (String identifier, UnitType type){
        parameters.put(identifier, type);
    }

}
