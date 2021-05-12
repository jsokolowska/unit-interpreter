package util.tree.statement;

import util.tree.expression.Expression;

public class AssignStatement extends Statement {
    private final String identifier;
    private final Expression assignExpression;

    public AssignStatement (String identifier, Expression assignExpression){
        this.identifier = identifier;
        this.assignExpression = assignExpression;
    }

}
