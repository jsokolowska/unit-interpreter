package util.tree.variable;

import util.tree.literal.BoolLiteral;

public class BoolVariable extends Variable{
    public BoolVariable(String name, boolean value){
        super(name, new BoolLiteral(value));
    }
}
