package source;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileSource implements Source{
    private final BufferedReader bufferedReader;
    private int currentChar;

    public FileSource (String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        bufferedReader = new BufferedReader(fileReader);
        currentChar = bufferedReader.read();
    }

    @Override
    public int get() throws IOException {
        int temp = currentChar;
        nextChar();
        return temp;
    }

    private void nextChar() throws IOException {
        currentChar = bufferedReader.read();
        if (currentChar == -1){
            currentChar = EOT;
        }
    }

}
