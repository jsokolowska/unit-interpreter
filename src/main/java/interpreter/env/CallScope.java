package interpreter.env;

import tree.Variable;

import java.util.Stack;

public class CallScope {
    private final Stack<BlockScope> blockScopes = new Stack<>();

    public CallScope(){
        blockScopes.add(new BlockScope());
    }

    public void addBlockScope(BlockScope scope){
        blockScopes.push(scope);
    }

    public void popBlockScope(){
        blockScopes.pop();
    }

    public BlockScope peekBlockScope(){
        return blockScopes.peek();
    }

    public void addVariable(Variable var){
        blockScopes.peek().addVariable(var);
    }

    public Variable getVariable(String id){
        return blockScopes.peek().getVariable(id);
    }

}
