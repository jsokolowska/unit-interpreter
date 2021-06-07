package util.tree.statement;

import util.tree.Variable;
import util.tree.expression.Expression;
import util.tree.type.Type;

public class VariableDeclarationStatement extends Statement{
    private final Variable variable;
    private final Expression expression;

    public VariableDeclarationStatement(Variable var, Expression expr){
        variable = var;
        expression = expr;
    }

    public VariableDeclarationStatement(Type type, String identifier, Expression expr){
        variable = new Variable(type, identifier);
        expression = expr;
    }

    @Override
    public String toString() {
        return variable.toString() + "=" + expression;
    }
}
