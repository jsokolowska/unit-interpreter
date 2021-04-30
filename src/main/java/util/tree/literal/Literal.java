package util.tree.literal;

public class Literal {
    private Object value;

    public Literal (Object value){
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
    public void setValue(Object value){
        this.value = value;
    }
}
