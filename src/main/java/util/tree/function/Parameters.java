package util.tree.function;

import util.tree.Node;
import util.tree.type.Type;

import java.util.*;

public class Parameters implements Node {
    private final SortedMap<String, Type> parameters = new TreeMap<>();

    public void addParameter (String identifier, Type type){
        parameters.put(identifier, type);
    }

    public boolean contains (String identifier, Type type){
        Type result;
        if((result = parameters.get(identifier) )!= null){
            return result == type;
        }
        return false;
    }
}
