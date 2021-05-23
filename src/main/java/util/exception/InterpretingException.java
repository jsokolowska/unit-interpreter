package util.exception;

public class InterpretingException extends RuntimeException{

    public InterpretingException(String message, int line){
        super("InterpretingException in line " + line + ": " + message);
    }

    public InterpretingException(String msg){
        super("InterpretingException: "  + msg);
    }
}
