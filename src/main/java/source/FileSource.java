package source;

public class FileSource implements Source{
    @Override
    public int get() {
        return 0;
    }

    @Override
    public void nextToken(){}

    public int add (int x, int y){
        return x+y;
    }
}
