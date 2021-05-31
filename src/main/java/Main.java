import interpreter.Interpreter;
import interpreter.env.Environment;
import parser.Parser;
import scanner.Scanner;
import source.FileSource;
import source.Source;
import source.StringSource;
import tree.type.TypeManager;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main (String [] args) throws IOException {
        if(args.length != 1){
            System.out.println("Usage\n \t interpreter.jar [input.txt]");
            return;
        }

        try{
            Source source = new FileSource(args[0]);
            Scanner scanner = new Scanner(source);
            Parser parser = new Parser(scanner);
            Environment env = new Environment();
            Interpreter interpreter = new Interpreter(parser.parse(), env);
            interpreter.execute();
        }catch (FileNotFoundException f){
            System.out.println("File does not exist");
        }
    }
}
