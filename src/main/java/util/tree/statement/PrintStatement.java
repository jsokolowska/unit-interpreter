package util.tree.statement;

import util.tree.function.Arguments;

public class PrintStatement extends Statement{
    private final Arguments arguments;

    public PrintStatement(Arguments arguments){
        this.arguments = arguments;
    }
}
