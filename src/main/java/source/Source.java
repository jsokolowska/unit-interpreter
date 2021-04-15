package source;

public interface Source {
    int get();
    void nextToken();

    int EOF = -1;
}
