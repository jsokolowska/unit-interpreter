package source;

public class StringSource implements Source{
    private final String text;
    private int currentPos;
    private int currentChar;

    public StringSource(String text){
        this.text = text;
        currentPos = 0;
        if (currentPos >= text.length()){
            currentChar = EOF;
        }else{
            currentChar = text.charAt(currentPos);
        }
    }
    @Override
    public int get() {
        return currentChar;
    }

    @Override
    public void nextToken() {
        currentPos += 1;
        if (currentPos < text.length()){
            currentChar = text.charAt(currentPos);
        }else{
            currentChar = EOF;
        }
    }
}
