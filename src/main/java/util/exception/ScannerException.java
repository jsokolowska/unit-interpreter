package util.exception;

import util.position.Position;

public class ScannerException extends RuntimeException{
    public ScannerException(Position pos, String message) {
        super("Scanner Exception in "  + pos + ": " + message);
    }
}
