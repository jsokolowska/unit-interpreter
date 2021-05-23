package tree.statement;


import interpreter.Visitor;

public class TypeStatement extends Statement{
    private final String identifier;

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
