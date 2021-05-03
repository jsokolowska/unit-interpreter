package util.tree.unit;

import util.tree.Node;

public class Unit implements Node {
    private String name;

    public Unit(String name){
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
