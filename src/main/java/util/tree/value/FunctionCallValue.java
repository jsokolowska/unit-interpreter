package util.tree.value;

import util.tree.statement.CallStatement;

public class FunctionCallValue {
    private CallStatement callStatement;

    public FunctionCallValue (CallStatement callStatement){
        this.callStatement = callStatement;
    }

    @Override
    public String toString() {
        return callStatement.toString();
    }
}
