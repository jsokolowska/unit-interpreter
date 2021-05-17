package util.tree.function;

import util.tree.Node;
import util.tree.statement.Statement;
import util.tree.type.Type;

public class Function implements Node {
    private final String identifier;
    private final Type returnType;
    private final Statement body;
    private final Parameters params;

    public Function(String identifier, Statement body, Parameters params, Type returnType){
        this.identifier = identifier;
        this.body = body;
        this.params = params;
        this.returnType = returnType;
    }

    public String getIdentifier() {
        return identifier;
    }

}
