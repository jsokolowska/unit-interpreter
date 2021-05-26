package tree.statement;

import interpreter.Visitor;
import tree.Variable;
import tree.expression.Expression;
import tree.type.Type;

public class VariableDeclarationStatement extends Statement{
    private final Variable variable;
    private final AssignStatement statement;

    public VariableDeclarationStatement(Variable var, AssignStatement assignStatement){
        variable = var;
        statement = assignStatement;
    }

    public VariableDeclarationStatement(Type type, String identifier){
        variable = new Variable(type, identifier);
        statement = null;
    }

    public Variable getVariable() {
        return variable;
    }

    public AssignStatement getAssignStatement() {
        return statement;
    }

    @Override
    public String toString() {
       if(statement!= null){
           return variable.toString() + "=" + statement.getAssignExpression().toString();
       }
        return variable.toString() + "=null";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
