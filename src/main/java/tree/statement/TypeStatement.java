package tree.statement;


import interpreter.Visitor;
import tree.expression.Expression;

public class TypeStatement extends Statement{
    //todo set expression instead of identifier and fix it in parser, interpreter and related tests
    private final Expression ex;

    public TypeStatement (Expression ex){
        this.ex = ex;
    }

    public Expression getExpression() {
        return ex;
    }

    @Override
    public String toString() {
        return "type:" + ex;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
