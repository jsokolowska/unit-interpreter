package util.exception;

public class CastingException extends RuntimeException{
    public CastingException (int line, String from, String to){
        super("Casting exception in line " + line + ": no available cast from " + from + " to " + to);
    }

}
