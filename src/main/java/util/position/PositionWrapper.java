package util.position;

import java.io.IOException;

public interface PositionWrapper {
    void advance(int character) throws IOException;
    int getColumn ();
    int getLine();
}
