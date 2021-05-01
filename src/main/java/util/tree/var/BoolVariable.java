package util.tree.var;

import util.tree.literal.BoolLiteral;

public class BoolVariable extends Variable{
    public BoolVariable(String name, boolean value){
        super(name, new BoolLiteral(value));
    }
}
