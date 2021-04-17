package source;

import java.io.IOException;

public interface Source {
    int get() throws IOException;
    int EOF = -1;
}
