package util.tree.type;

public class Type {

    @Override
    public boolean equals(Object obj) {
         return obj.getClass() == this.getClass();
    }
}
