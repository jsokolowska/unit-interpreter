package util.position;

public class Position {
    private int line;
    private int column;

    public Position(){
        this(1, 0);
    }
    public Position(int line, int column){
        this.line = line;
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }
    public void advanceLine(){
        line ++;
        column = 0;
    }
    public void advanceColumn(){
        column ++;
    }
    public String toString(){
        return "line"+line + " col" + column;
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(obj.getClass()!= this.getClass()){
            return false;
        }
        Position pos = (Position) obj;
        boolean c = pos.getColumn() == this.getColumn();
        boolean l = pos.getLine() == this.getLine();
        return c && l;
    }

    @Override
    public Position clone(){
        return new Position(line, column);
    }
}
