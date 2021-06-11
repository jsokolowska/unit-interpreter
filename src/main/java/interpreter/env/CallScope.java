package interpreter.env;

import tree.Variable;
import tree.value.Literal;

import java.util.ListIterator;
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
        ListIterator<BlockScope> listIterator = blockScopes.listIterator(blockScopes.size());
        while(listIterator.hasPrevious()){
            Variable value = listIterator.previous().getVariable(id);
            if(value != null){
                return value;
            }
        }
        return null;
    }

    public boolean variableExistsInBlock(String id){
        return blockScopes.peek().variableExists(id);
    }

    public boolean variableExistsInCallScope(String id){
        ListIterator<BlockScope> listIterator = blockScopes.listIterator(blockScopes.size());
        boolean exists = false;
        while(listIterator.hasPrevious()){
             exists = exists || listIterator.previous().variableExists(id);
        }
        return exists;
    }
}
