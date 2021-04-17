package util.position;

import source.Source;
import util.position.Position;

import java.io.IOException;

public class PositionWrapper implements Source{
    private final Source source;
    private final Position position; // position of char that was last extracted from source

    public PositionWrapper(Source source){
        this.position = new Position();
        this.source = source;
    }
    @Override
    public int get() throws IOException {
        int character = source.get();
        advance(character);
        return character;
    }

    public void advance(int character) {
        if (character == '\n') {
            position.advanceLine();
        } else {
            position.advanceColumn();
        }
    }
    public int getColumn (){
        return position.getColumn();
    }
    public int getLine(){
        return position.getLine();
    }
    public Position getPosition(){return position;}
}
