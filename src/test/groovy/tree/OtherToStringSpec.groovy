package tree

import spock.lang.Specification
import tree.expression.Expression
import tree.function.Arguments
import tree.statement.AssignStatement
import tree.statement.BreakStatement
import tree.statement.ContinueStatement
import tree.statement.PrintStatement
import tree.statement.ReturnStatement
import tree.statement.TypeStatement
import tree.statement.VariableDeclarationStatement
import tree.type.IntType
import tree.value.FunctionCall

class OtherToStringSpec extends Specification{

    def "Check AssignStatement toString"(){
        given:
        var stmt = new AssignStatement("id", expr)

        expect:
        stmt.toString() == "id" +"=" + expr.toString()

        where:
         expr  << [null, new Expression()]
    }

    def "Check simple Statements toString"(){
        expect:
        expr.toString() == str

        where:
        expr                                        || str
        new BreakStatement()                        || "break"
        new ContinueStatement()                     || "continue"
        new PrintStatement(new Arguments())         || "print(" + new Arguments().toString() + ")"
        new ReturnStatement()                       || "return:null"
        new ReturnStatement(new Expression())       || "return:" + new Expression().toString()
        new TypeStatement(new Expression())                     || "type:u"
        new VariableDeclarationStatement(new Variable(new IntType(), "id"),
                new AssignStatement("id", new Expression())) || "int:id=u"
    }

    def "Check funcall to string test"(){
        given:
        var funCall = new FunctionCall("id", new Arguments())

        expect:
        funCall.toString() == "id" + "(" + new Arguments().toString() + ")"
    }

    def "Check arguments toString" (){
        given:
        var arg = new Arguments()

        when:
        arg.addArgument(new Expression())
        arg.addArgument(new Expression())

        then:
        arg.toString() == "u, u"
    }

    def "Check empty arguments to string"(){
        expect:
        new Arguments().toString() == "none"
    }

}
