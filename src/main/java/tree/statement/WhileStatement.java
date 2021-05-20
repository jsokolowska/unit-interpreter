package tree.statement;

import tree.expression.Expression;

public class WhileStatement extends Statement{
    private final Statement body;
    private final Expression condition;

    public WhileStatement (Statement body, Expression condition){
        this.body = body;
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "while(" + condition + "):" +body;
    }
}
