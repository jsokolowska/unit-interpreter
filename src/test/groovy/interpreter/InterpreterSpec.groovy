package interpreter

import interpreter.env.Environment
import spock.lang.Specification
import tree.Program
import tree.Variable
import tree.type.IntType
import util.exception.InterpretingException

class InterpreterSpec extends Specification{

    def "Should throw exception when program does not have main function"(){
        given:
        var interpreter = new Interpreter(new Program(), null, null);

        when:
        interpreter.execute()

        then:
        thrown(InterpretingException)
    }

    def prepEnv(){
        var env = new Environment()
        env.pushNewCallScope()
        env.pushNewBlock()
        return env
    }

    def "Should throw exception when a variable is redefined" (){
        given:
        var env = prepEnv()
        var variable = new Variable(new IntType(), "var")
        var interpreter = new Interpreter(new Program(), null, env)
        env.addVariable(variable)

        when:
        interpreter.visit(variable)

        then:
        thrown(InterpretingException)
    }

    def "Should add variable to the stack when visiting it"(){
        given:
        var env = prepEnv()
        var variable = new Variable(new IntType(), "var")
        var interpreter = new Interpreter(new Program(), null, env)

        when:
        interpreter.visit(variable)

        then:
        variable == env.getVariable("var")
    }

}
