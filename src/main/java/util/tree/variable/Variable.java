package util.tree.variable;

import util.tree.literal.Literal;

public class Variable {
    private String name;
    private Literal value;

    public Variable(String name, Literal value){
        this.name = name;
        this.value = value;
    }

    public Literal getValue() {
        return value;
    }
    public String getName(){
        return name;
    }
}
