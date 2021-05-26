package tree.statement;

import interpreter.Visitor;
import tree.value.FunctionCall;

public class CallStatement extends Statement{
    private final FunctionCall funCall;

    public CallStatement(FunctionCall call){
        this.funCall = call;
    }

    public FunctionCall getFunCall() {
        return funCall;
    }

    @Override
    public String toString() {
        return funCall.toString();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
