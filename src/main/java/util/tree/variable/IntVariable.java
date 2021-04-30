package util.tree.variable;

import util.tree.literal.IntLiteral;

public class IntVariable extends Variable {
    public IntVariable (String name, Integer value){
        super(name, new IntLiteral(value));
    }
}
