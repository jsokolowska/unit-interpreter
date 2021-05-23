package tree.type;

import tree.Visitable;

public abstract class Type {

    @Override
    public boolean equals(Object obj) {
         return obj.getClass() == this.getClass();
    }

    public abstract String prettyToString();
}
