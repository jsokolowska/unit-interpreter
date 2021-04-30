package util.tree.variable;

import util.tree.literal.StringLiteral;

public class StringVariable extends Variable{
    public StringVariable (String name, String value){
        super(name, new StringLiteral(value));
    }
}
