package tree.statement;


import tree.expression.Expression;

public class ReturnStatement extends Statement{
    private final Expression returnExpression;

    public ReturnStatement (Expression returnExpression){
        this.returnExpression = returnExpression;
    }

    public ReturnStatement (){
        this(null);
    }

    @Override
    public String toString() {
        return "return:" + returnExpression;
    }
}
