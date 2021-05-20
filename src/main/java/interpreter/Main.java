package interpreter;

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
        Interpreter interpreter = new Interpreter(parser, typeManager);
        interpreter.execute();
    }
}
