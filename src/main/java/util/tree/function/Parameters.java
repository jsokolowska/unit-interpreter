package util.tree.function;

import util.tree.Node;
import util.tree.type.Type;

import java.util.*;

public class Parameters implements Node {
    private final SortedMap<String, Type> parameters = new TreeMap<>();

    public void addParameter (String name, Type type){
        parameters.put(name, type);
    }

    public boolean contains (String name, Type type){
        Type result;
        if((result = parameters.get(name) )!= null){
            return result == type;
        }
        return false;
    }
}
