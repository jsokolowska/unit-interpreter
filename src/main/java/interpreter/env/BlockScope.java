package interpreter.env;

import tree.Variable;

import java.util.HashMap;
import java.util.Map;

public class BlockScope {
    private final Map<String, Variable> variables = new HashMap<>();

    public void addVariable(Variable var){
        variables.put(var.getIdentifier(), var);
    }

    public Variable getVariable(String id){
        return variables.get(id);
    }
}
