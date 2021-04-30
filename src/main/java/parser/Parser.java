package parser;

import scanner.Scanner;
import util.tree.Program;

public class Parser {
    private final Scanner scanner;

    public Parser (Scanner scanner){
        this.scanner = scanner;
    }

    public Program parse(){
        return new Program();
    }


}
