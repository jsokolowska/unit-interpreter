package util.tree.statement;

import util.tree.expression.Expression;

public class IfElseStatement extends Statement{
    private Expression ifCondition;
    private Statement ifStatement;
    private Statement elseStatement;
}
