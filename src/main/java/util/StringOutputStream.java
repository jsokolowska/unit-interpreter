package util;

import java.io.*;

public class StringOutputStream extends OutputStream {
    StringBuilder str = new StringBuilder();

    public String getStringValue(){
        return str.toString();
    }

    @Override
    public void write(int b) throws IOException {
        str.append((char) b);
    }
}