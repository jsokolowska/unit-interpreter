package tree.function;

import interpreter.Visitor;
import tree.AbstractFunction;
import tree.Visitable;
import tree.statement.Statement;
import tree.type.Type;

public class Function extends AbstractFunction implements Visitable {
    private final String identifier;
    private final Type returnType;
    private final Statement body;

    public Function(String identifier, Statement body, Parameters params, Type returnType){
        this.identifier = identifier;
        this.body = body;
        this.params = params;
        this.returnType = returnType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
