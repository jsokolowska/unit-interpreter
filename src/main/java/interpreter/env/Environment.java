package interpreter.env;

import interpreter.util.Casting;
import interpreter.util.StackValue;
import tree.Variable;
import tree.type.Type;
import tree.value.Literal;

import java.util.Stack;

public class Environment {
    private final Stack<CallScope> callScopes;
    private final Stack<StackValue> values;
    private boolean returned;
    private boolean broken;
    private boolean continued;
    private boolean returnedWithValue;

    public Environment() {
        this.values = new Stack<>();
        this.callScopes = new Stack<>();
    }

    public void pushNewCallScope() {
        this.callScopes.push(new CallScope());
    }

    public void pushNewBlock() {
        callScopes.peek().addBlockScope(new BlockScope());
    }

    public void popBlock() {
        callScopes.peek().popBlockScope();
    }

    public void popCallScope() {
        callScopes.pop();
    }

    public void addVariable(Variable var) {
        callScopes.peek().addVariable(var);
    }

    public boolean variableExistsInBlock(String id) {
        return callScopes.peek().variableExistsInBlock(id);
    }

    public Variable getVariable(String id) {
        return callScopes.peek().getVariable(id);
    }

    public void pushValue(Literal<?> value) {
        Type type = Casting.getMatchingType(value);
        values.push(new StackValue(value, type));
    }

    public void pushValue(StackValue val) {
        values.push(val);
    }

    public void pushValue(Literal<?> val, Type type) {
        values.push(new StackValue(val, type));
    }

    public StackValue popValue() {
        return values.pop();
    }

    public boolean isStackEmpty() {
        return values.size() == 0;
    }

    public boolean hasBroken() {
        return broken;
    }

    public boolean hasReturned() {
        return returned;
    }

    public boolean hasContinued() {
        return continued;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public void setContinued(boolean continued) {
        this.continued = continued;
    }

    public void setReturnedWithValue(boolean returnedWithValue) {
        this.returnedWithValue = returnedWithValue;
    }

    public boolean hasReturnedWithValue() {
        return returnedWithValue;
    }
}
