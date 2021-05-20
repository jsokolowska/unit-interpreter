package tree;

import interpreter.Visitor;

public interface Visitable {
    void accept(Visitor visitor);
}
