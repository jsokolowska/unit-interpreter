package tree.expression.operator;

import interpreter.Interpreter;
import interpreter.Visitor;
import tree.Visitable;

public abstract class Operator{
    protected Operator(){}

    public abstract void accept(Visitor visitor);
}
