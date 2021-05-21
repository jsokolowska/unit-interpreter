package interpreter.env;

import java.util.Stack;

public class Environment {
    private final Stack<Scope> scopes;
    private final Stack<CallContext> callContexts;

    public Environment(){
        this.scopes = new Stack<>();
        this.callContexts = new Stack<>();
    }
}
