package util.tree.function;

import util.tree.Node;
import util.tree.type.Type;

import java.util.ArrayList;
import java.util.List;

public class Parameters implements Node {
    private List<Type> parameters = new ArrayList<>();

    private void addParameter (Type type){
        parameters.add(type);
    }
}
