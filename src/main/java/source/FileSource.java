package source;

import java.io.*;

public class FileSource implements Source{
    private final BufferedReader bufferedReader;
    private int currentChar;

    public FileSource (String filePath) throws FileNotFoundException {
        FileReader fileReader = new FileReader(filePath);
        bufferedReader = new BufferedReader(fileReader);
    }

    @Override
    public int get() throws IOException {
        int temp = currentChar;
        nextChar();
        return temp;
    }

    public void nextChar() throws IOException {
        currentChar = bufferedReader.read();
        if (currentChar == -1){
            currentChar = EOF;
        }
    }

}
