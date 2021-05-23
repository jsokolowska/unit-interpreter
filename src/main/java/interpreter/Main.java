package interpreter;

import interpreter.env.Environment;
import parser.Parser;
import scanner.Scanner;
import source.Source;
import source.StringSource;
import tree.type.TypeManager;

import java.io.IOException;

public class Main {
    public static void main (String [] args) throws IOException {
        Source source = new StringSource("int main(){break;}");
        Scanner scanner = new Scanner(source);
        TypeManager typeManager = new TypeManager();
        Parser parser = new Parser(scanner, typeManager);
        Environment env = new Environment();
        Interpreter interpreter = new Interpreter(parser.parse(), typeManager, env);
        interpreter.execute();
    }
}
