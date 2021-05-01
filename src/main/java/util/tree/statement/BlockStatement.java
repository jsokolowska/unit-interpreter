package util.tree.statement;

import java.util.ArrayList;
import java.util.List;

public class BlockStatement extends Statement{
    private List<Statement> statements = new ArrayList<>();

    public void add (Statement stmt){
        statements.add(stmt);
    }
}
