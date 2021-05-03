package util.tree.function;

import util.tree.statement.Statement;

public class Function {
    private String name;
    private Statement body;

    public Function(){}

    public Function(String name, Statement body){
        this.name = name;
        this.body = body;
    }

    public String getName() {
        return name;
    }
}
