package util.tree.value.literal;

public class IntLiteral extends Literal {
    public IntLiteral (Integer value){
        super(value);
    }

    @Override
    public String toString() {
        return ((Integer) value).toString();
    }
}
