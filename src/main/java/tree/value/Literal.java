package tree.value;

public class Literal<T> extends Value{
    T value;
    public Literal (T value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
