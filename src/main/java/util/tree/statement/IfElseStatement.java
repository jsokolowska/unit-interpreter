package util.tree.statement;

import util.tree.expression.Expression;

public class IfElseStatement extends Statement{
    private final Expression ifCondition;
    private final Statement ifStatement;
    private final Statement elseStatement;

    public IfElseStatement(Expression ifCondition, Statement ifStatement, Statement elseStatement){
        this.ifCondition = ifCondition;
        this.ifStatement = ifStatement;
        this.elseStatement = elseStatement;
    }
    public IfElseStatement(Expression ifCondition, Statement ifStatement){
        this(ifCondition, ifStatement, null);
    }

    @Override
    public String toString() {
        return "if(" + ifCondition + ")<" + ifStatement + ">\nelse<" + elseStatement + ">";
    }
}
