package util.tree.function;


import util.tree.type.Type;

public class Argument {
    private Type value;
    private String name;

    public Argument(String  name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
