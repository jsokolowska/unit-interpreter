package tree.statement;


import interpreter.Visitor;
import tree.expression.Expression;

public class TypeStatement extends Statement{
    private final String identifier;
    //todo set expression instead of identifier and fix it in parser, interpreter and related tests
    private final Expression ex;

    public TypeStatement (String identifier){
        this.identifier = identifier;
    }

    public String getIdentifier(){
        return identifier;
    }

    @Override
    public String toString() {
        return "type:" + identifier;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
