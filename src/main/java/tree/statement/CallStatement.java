package tree.statement;

import tree.value.FunctionCall;

public class CallStatement extends Statement{
    private final FunctionCall funCall;
    

    public CallStatement(FunctionCall call){
        this.funCall = call;
    }


    @Override
    public String toString() {
        return funCall.toString();
    }

}
