package interpreter.env;

import tree.Variable;

import java.util.Stack;

public class Environment {
    private final Stack<CallScope> callScopes;
    private final Stack<PartialValue> values;

    public Environment(){
        this.values = new Stack<>();
        this.callScopes = new Stack<>();
    }
    public void pushNewCallScope(){
        this.callScopes.push( new CallScope());
    }

    public void pushNewBlock(){
        callScopes.peek().addBlockScope( new BlockScope());
    }

    public void popBlock(){
        callScopes.peek().popBlockScope();
    }

    public void pushCallScope(CallScope scope){
        callScopes.push(scope);
    }

    public void popCallScope(){
        callScopes.pop();
    }

    public void addVariable(Variable var){
        callScopes.peek().addVariable(var);
    }

    public Variable getVariable(String id){
        return callScopes.peek().getVariable(id);
    }
}
