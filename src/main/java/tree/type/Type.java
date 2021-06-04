package tree.type;


public abstract class Type {

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
         return obj.getClass() == this.getClass();
    }

    public abstract String prettyToString();
}
