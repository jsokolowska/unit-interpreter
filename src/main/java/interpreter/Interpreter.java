package interpreter;

import interpreter.env.Environment;
import tree.Program;
import tree.Variable;
import tree.Visitable;
import tree.expression.math.*;
import tree.expression.unit.ConversionExpression;
import tree.expression.unit.MulUnitExpression;
import tree.expression.unit.PowerUnitExpression;
import tree.expression.unit.UnaryUnitExpression;
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

public class Interpreter implements Visitor{
    private Program program;
    private TypeManager typeManager;
    private Environment env;
    private int line;

    Interpreter(Program program, TypeManager typeManager, Environment env) throws IOException {
        this.program = program;
        this.typeManager = typeManager;
        this.env = env;
    }

    public void execute() throws IOException {
        program.accept(this);
    }

    public void visit(Visitable visitable){
        //todo
        System.out.println("Visitable");
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

    public void visit(FunctionCall functionCall){
        System.out.println("FunctionCall");
    }

    public void visit(Literal<?> literal){
        //todo

        System.out.println("Literal<?>");
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
    public void visit(Conversion conversion){
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
        //todo
        System.out.println("Assign");
    }
    public void visit(BlockStatement statement){
        //create new block scope
        env.pushNewBlock();
        for (var stmt : statement.getStatements()){
            stmt.accept(this);
        }
        //delete scope
        env.popBlock();
    }
    public void visit(BreakStatement statement){
        //todo
        System.out.println("BREAK");
    }
    public void visit(CallStatement statement){
        //todo
        System.out.println("CallSTMT");
    }
    public void visit(ContinueStatement statement){
        //todo
        System.out.println("ContinueStmt");
    }
    public void visit(ExplainStatement statement){
        //todo
        System.out.println("ExplainStmt");
    }
    public void visit(IfElseStatement statement){
        //todo
        System.out.println("Ifelse");
    }
    public void visit(PrintStatement statement){
        //todo
        System.out.println("Print");
    }
    public void visit(ReturnStatement statement){
        //todo
        System.out.println("Return");
    }
    public void visit(TypeStatement statement){
        //todo
        String id = statement.getIdentifier();

        System.out.println();
    }
    public void visit(VariableDeclarationStatement statement){
        //todo
        System.out.println("Vardcl");
    }
    public void visit(WhileStatement statement){
        //todo
        System.out.println("While");
    }

    public void visit(Function function){
        //todo

        System.out.println("Function");
        Statement stmt = function.getBody();
        stmt.accept(this);
    }
    public void visit(Arguments arguments){
        //todo
        System.out.println("Arguments");
    }
    public void visit(Parameters parameters){
        //todo
        System.out.println("Parameters");
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
