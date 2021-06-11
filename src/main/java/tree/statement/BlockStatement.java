package tree.statement;

import interpreter.Visitor;

import java.util.ArrayList;
import java.util.List;

public class BlockStatement extends Statement{
    private final List<Statement> statements = new ArrayList<>();

    public void add (Statement stmt){
        statements.add(stmt);
    }

    public List<Statement> getStatements(){
        return statements;
    }

    @Override
    public String toString() {
        StringBuilder str =  new StringBuilder("{");
        for(Statement stmt: statements){
            str.append(stmt);
            str.append("\n");
        }
        return str + "}";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
