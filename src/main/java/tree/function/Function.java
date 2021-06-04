package tree.function;

import interpreter.Visitor;
import tree.AbstractFunction;
import tree.Visitable;
import tree.statement.Statement;
import tree.type.Type;

public class Function extends AbstractFunction implements Visitable {
    private final Type returnType;
    private final Statement body;

    public Function(String identifier, Statement body, Parameters params, Type returnType){
        this.name = identifier;
        this.body = body;
        this.params = params;
        this.returnType = returnType;
    }

    public Statement getBody() {
        return body;
    }

    public Type getReturnType() {
        return returnType;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
