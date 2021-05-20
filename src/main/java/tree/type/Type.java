package tree.type;

import tree.Node;

public abstract class Type implements Node {

    @Override
    public boolean equals(Object obj) {
         return obj.getClass() == this.getClass();
    }
}
