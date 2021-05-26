package tree.statement;


import interpreter.Visitor;
import tree.expression.Expression;

public class ReturnStatement extends Statement{
    private final Expression returnExpression;

    public ReturnStatement (Expression returnExpression){
        this.returnExpression = returnExpression;
    }

    public ReturnStatement (){
        this(null);
    }

    public Expression getReturnExpression() {
        return returnExpression;
    }

    @Override
    public String toString() {
        return "return:" + returnExpression;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
