package util.tree.variable;

import util.tree.literal.FloatLiteral;

public class FloatVariable extends Variable{
    public FloatVariable (String name, Double value){
        super(name, new FloatLiteral(value));
    }
}
