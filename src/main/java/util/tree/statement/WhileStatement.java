package util.tree.statement;

import util.tree.Expression;

public class WhileStatement extends Statement{
    private Statement body;
    private Expression condition;

    public WhileStatement (Statement body, Expression condition){
        this.body = body;
        this.condition = condition;
    }
}
