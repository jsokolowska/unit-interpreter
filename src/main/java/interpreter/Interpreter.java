package interpreter;

import interpreter.env.Environment;
import tree.AbstractFunction;
import tree.Program;
import tree.Variable;
import tree.Visitable;
import tree.expression.Expression;
import tree.expression.math.*;
import tree.expression.unit.*;
import tree.expression.unit.value.UnitExpressionLiteral;
import tree.expression.unit.value.UnitExpressionVariableValue;
import tree.function.Arguments;
import tree.function.Function;
import tree.function.Parameters;
import tree.statement.*;
import tree.type.TypeManager;
import tree.unit.*;
import tree.value.FunctionCall;
import tree.value.Literal;
import tree.value.VariableValue;
import util.exception.InterpretingException;

import java.io.IOException;
import java.util.List;

public class Interpreter implements Visitor{
    private final Program program;
    private final TypeManager typeManager;
    private final Environment env;
    private Integer line;

    public Interpreter(Program program, TypeManager typeManager, Environment env) throws IOException {
        this.program = program;
        this.typeManager = typeManager;
        this.env = env;
        line = 0;
    }

    public void execute() throws IOException {
        program.accept(this);
    }

    protected int getLine(){
        return line;
    }

    public void visit(Visitable visitable){
        throw new RuntimeException("Unrecognized Visitable");
    }
    public void visit(Program program){
        Function main = program.getFunction("main");
        if(main == null){
            throw new InterpretingException("Program must contain function main");
        }
        main.accept(this);
    }
    public void visit(Variable var){
        Variable known = env.getVariable(var.getIdentifier());
        if(known != null)
            throw new InterpretingException("Variable redefinition " + var.getIdentifier(), line);

        env.addVariable(var);
        System.out.println("Variable");
    }

    //todo test
    public void visit(FunctionCall functionCall){
        env.pushNewCallScope();
        String funName = functionCall.getIdentifier();

        AbstractFunction function = program.getFunctionOrConversionFunction(funName);
        if(function == null){
            throw new InterpretingException("Unknown function or conversion identifier " + funName, line);
        }
        Parameters params = function.getParams();
        params.accept(this);
        List<Expression> args = functionCall.getArgs().getArgList();

        //todo evaluate expressions, check types and set values for variables in block context
        //todo think it over

        function.accept(this);

        env.popCallScope();
    }

    public void visit(Function function){
        //todo execute statements within block context

        System.out.println("Function");
        Statement stmt = function.getBody();
        stmt.accept(this);
    }

    //todo tests
    public void visit(Parameters parameters){
        var paramMap = parameters.getParameters();
        for (String key : paramMap.keySet()){
            Variable var = new Variable(paramMap.get(key),key);
            env.addVariable(var);
        }
    }



    public void visit(Literal<?> literal){
        env.pushValue(literal);
    }

    public void visit(VariableValue variableValue){
        Variable variable = env.getVariable(variableValue.getIdentifier());
        if(variable == null){
            throw new InterpretingException("Unknown identifier: " + variableValue.getIdentifier(), line);
        }

        //todo
        System.out.println("VariableValue");
    }
    public void visit(CompoundExpr expr){
        //todo
        System.out.println("CompoundExpr");
    }
    public void visit(CompoundTerm term){
        //todo
        System.out.println("CompoundTerm");
    }
    public void visit(ConversionFunction conversionFunction){
        //todo
        System.out.println("Conv");
    }
    public void visit(UnitDeclaration unitDeclaration){
        //todo
        System.out.println("UnitDCL");
    }
    public void visit(UnitParameters parameters){
        //todo
        System.out.println("UnitParameters");
    }

    public void visit(AssignStatement statement){
        line = statement.getLine();
        //todo
        System.out.println("Assign");
    }
    public void visit(BlockStatement statement){
        //todo
        line = statement.getLine();
        //create new block scope
        env.pushNewBlock();
        for (var stmt : statement.getStatements()){
            stmt.accept(this);
        }
        //delete scope
        env.popBlock();
    }
    public void visit(BreakStatement statement){
        line = statement.getLine();
        //todo
        System.out.println("BREAK");
    }
    public void visit(CallStatement statement){
        line = statement.getLine();
        //todo
        System.out.println("CallSTMT");
    }
    public void visit(ContinueStatement statement){
        line = statement.getLine();
        //todo
        System.out.println("ContinueStmt");
    }
    public void visit(ExplainStatement statement){
        line = statement.getLine();
        //todo
        System.out.println("ExplainStmt");
    }
    public void visit(IfElseStatement statement){
        line = statement.getLine();
        //todo
        System.out.println("Ifelse");
    }
    public void visit(PrintStatement statement){
        line = statement.getLine();
        //todo
        System.out.println("Print");
    }
    public void visit(ReturnStatement statement){
        line = statement.getLine();
        //todo
        System.out.println("Return");
    }
    public void visit(TypeStatement statement){
        line = statement.getLine();
        //todo
        String id = statement.getIdentifier();

        System.out.println();
    }
    public void visit(VariableDeclarationStatement statement){
        line = statement.getLine();
        //todo
        System.out.println("Vardcl");
    }
    public void visit(WhileStatement statement){
        line = statement.getLine();
        //todo
        System.out.println("While");
    }

    public void visit(ConversionExpression expression){
        //todo
        System.out.println("ConvExpr");
    }
    public void visit(MulUnitExpression expression){
        //todo
        System.out.println("MulUnitExpr");
    }
    public void visit(PowerUnitExpression expression){
        //todo
        System.out.println("PowerUnitExpr");
    }
    public void visit(UnaryUnitExpression expression){
        //todo
        System.out.println("UnaryUnitExpr");
    }
    public void visit(UnitExpressionLiteral<?> literal){
        //todo
        System.out.println("UnitLiteral");
    }
    public void visit(UnitExpressionVariableValue variableValue){
        //todo
        System.out.println("UnitVarVal");
    }

    public void visit(AndExpression expression){
        //todo
        System.out.println("AndExpr");
    }
    public void visit(ArithmeticException expression){
        //todo
        System.out.println("ArithmeticExpr");
    }
    public void visit(ComparisonExpression expression){
        //todo
        System.out.println("CompExpr");
    }
    public void visit(MultiplyExpression expression){
        //todo
        System.out.println("MultExpr");
    }
    public void visit(OrExpression expression){
        //todo
        System.out.println("OrExpr");
    }
    public void visit(PowerExpression expression){
        //todo
        System.out.println("PowerExpr");
    }
    public void visit(RelationalExpression expression){
        //todo
        System.out.println("RelationalExpression");
    }
    public void visit(UnaryExpression expression){
        //todo
        System.out.println("UnaryExpr");
    }
}
