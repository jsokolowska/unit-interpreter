package util.tree.statement;


import util.tree.math.Expression;

public class ReturnStatement extends Statement{
    private final Expression returnExpression;

    public ReturnStatement (Expression returnExpression){
        this.returnExpression = returnExpression;
    }

    public ReturnStatement (){
        this(null);
    }
}
