package util.tree.unit;

import util.Token;

public class CustomUnit {
    private final String name;

    public CustomUnit(Token unitToken){
        name = unitToken.getStringValue();
    }

    public String getName() {
        return name;
    }
}
