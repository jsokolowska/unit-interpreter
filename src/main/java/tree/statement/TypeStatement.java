package tree.statement;


public class TypeStatement extends Statement{
    private final String identifier;

    public TypeStatement (String identifier){
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "type:" + identifier;
    }
}
