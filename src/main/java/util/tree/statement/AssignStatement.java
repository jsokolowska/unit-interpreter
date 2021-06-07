package util.tree.statement;

import util.tree.expression.Expression;

public class AssignStatement extends Statement {
    private final String identifier;
    private final Expression assignExpression;

    public AssignStatement (String identifier, Expression assignExpression){
        this.identifier = identifier;
        this.assignExpression = assignExpression;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Expression getAssignExpression() {
        return assignExpression;
    }

    @Override
    public String toString() {
        return identifier + "=" + assignExpression;
    }
}