package tree.statement;

import interpreter.Visitor;
import tree.function.Arguments;

public class PrintStatement extends Statement{
    private final Arguments arguments;

    public PrintStatement(Arguments arguments){
        this.arguments = arguments;
    }

    public Arguments getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "print(" + arguments + ")";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
