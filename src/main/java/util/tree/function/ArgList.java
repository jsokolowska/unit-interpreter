package util.tree.function;

import util.tree.var.Argument;

import java.util.HashMap;
import java.util.Map;

public class ArgList {
    private final Map<String, Argument> argList = new HashMap<>();

    public void add (Argument var){
        argList.put(var.getName(), var);
    }
}
