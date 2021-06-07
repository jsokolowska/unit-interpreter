package util.tree.value.literal;

public class FloatLiteral extends Literal{
    public FloatLiteral (Double value){
        super(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}