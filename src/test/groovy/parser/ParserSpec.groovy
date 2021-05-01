package parser

import source.Source
import source.StringSource
import scanner.Scanner
import spock.lang.*
import util.tree.statement.ReturnStatement;

class ParserSpec extends Specification{
    def "Should parse different return statements"(){
        given: "Parser that uses string source"
        def source = new StringSource(str)
        def scanner = new Scanner(source)
        def parser = new Parser(scanner)
        def result;

        when: "trying to parse Statement"
        result = parser.parseStatements()

        then: "A not null instance of ReturnStatement class is parsed"
        result != null;
        result instanceof ReturnStatement

        where:
        str << ["return ;", "return 2;", "return _a;"]
    }

}
