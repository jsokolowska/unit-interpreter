package source;

import java.io.IOException;

public class StringSource implements Source{
    private final String text;
    private int currentPos;
    private int currentChar;

    public StringSource(String text) throws IOException {
        if (text == null){
            throw new IOException("No string provided");
        }
        this.text = text;
        currentPos = 0;
        if (currentPos >= text.length()){
            currentChar = EOT;
        }else{
            currentChar = text.charAt(currentPos);
        }
    }
    @Override
    public int get() {
        int temp = currentChar;
        nextChar();
        return temp;
    }


    public void nextChar() {
        currentPos += 1;
        if (currentPos < text.length()){
            currentChar = text.charAt(currentPos);
        }else{
            currentChar = EOT;
        }
    }

}
