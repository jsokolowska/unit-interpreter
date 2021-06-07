package tree.statement;

import tree.Visitable;

public abstract class Statement implements Visitable {
    private int line;

    public int getLine() {
        return line;
    }

    public void setLine(int line)
    {
        this.line = line;
    }
}
