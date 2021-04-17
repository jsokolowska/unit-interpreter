package util.position;

import java.io.IOException;

public class ScannerPositionWrapper implements PositionWrapper {
    @Override
    public void advance(int character) {

    }

    @Override
    public int getColumn() {
        return 0;
    }

    @Override
    public int getLine() {
        return 0;
    }
}
