package tree.statement;

import interpreter.Visitor;
import tree.expression.Expression;

public class WhileStatement extends Statement{
    private final Statement body;
    private final Expression condition;

    public WhileStatement (Statement body, Expression condition){
        this.body = body;
        this.condition = condition;
    }

    public Statement getBody() {
        return body;
    }

    public Expression getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "while(" + condition + "):" +body;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
