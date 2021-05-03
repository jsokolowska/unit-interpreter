package util.tree.function;

import util.tree.statement.Statement;

public class Function {
    private String name;
    private Statement body;
    private Parameters params;

    public Function(){}

    public Function(String name, Statement body, Parameters params){
        this.name = name;
        this.body = body;
        this.params = params;
    }

    public String getName() {
        return name;
    }
}
