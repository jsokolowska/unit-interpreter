package util.tree.var;

import util.tree.literal.IntLiteral;

public class IntVariable extends Variable {
    public IntVariable (String name, Integer value){
        super(name, new IntLiteral(value));
    }
}
