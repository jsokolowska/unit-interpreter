package interpreter;

import interpreter.env.Environment;
import parser.Parser;
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

import java.io.IOException;
import java.util.Map;

public class Interpreter implements Visitor{
    private Parser parser;
    private TypeManager typeManager;
    private Environment env;

    Interpreter(Parser parser, TypeManager typeManager, Environment env){
        this.parser = parser;
        this.typeManager = typeManager;
        this.env = env;
    }

    public void execute() throws IOException {
        Program program = parser.parse();
        visit(program);
    }

    public void visit(Visitable visitable){
        System.out.println("Visitable");
    }
    public void visit(Program program){
        System.out.println("Program");
        for (var dcl : program.getUnitDcls()){
            dcl.accept(this);
        }

        for(var conv : program.getConversions()){
            conv.accept(this);
        }

        Map<String, Function> functions = program.getFunctions();

        Function main = functions.get("main");
        main.accept(this);


    }
    public void visit(Variable var){
        System.out.println("Variable");
    }
    public void visit(FunctionCall functionCall){
        System.out.println("FunctionCall");
    }
    public void visit(Literal<?> literal){
        System.out.println("Literal<?>");
    }
    public void visit(VariableValue variableValue){
        System.out.println("VariableValue");
    }
    public void visit(CompoundExpr expr){
        System.out.println("CompoundExpr");
    }
    public void visit(CompoundTerm term){
        System.out.println("CompoundTerm");
    }
    public void visit(Conversion conversion){
        System.out.println("Conv");
    }
    public void visit(UnitDeclaration unitDeclaration){
        System.out.println("UnitDCL");
    }
    public void visit(UnitParameters parameters){
        System.out.println("UnitParameters");
    }

    public void visit(AssignStatement statement){
        System.out.println("Assign");
    }
    public void visit(BlockStatement statement){
        System.out.println("Block");
    }
    public void visit(BreakStatement statement){
        System.out.println("BREAK");
    }
    public void visit(CallStatement statement){
        System.out.println("CallSTMT");
    }
    public void visit(ContinueStatement statement){
        System.out.println("ContinueStmt");
    }
    public void visit(ExplainStatement statement){
        System.out.println("ExplainStmt");
    }
    public void visit(IfElseStatement statement){
        System.out.println("Ifelse");
    }
    public void visit(PrintStatement statement){
        System.out.println("Print");
    }
    public void visit(ReturnStatement statement){
        System.out.println("Return");
    }
    public void visit(TypeStatement statement){
        System.out.println("Typestmt");
    }
    public void visit(VariableDeclarationStatement statement){
        System.out.println("Vardcl");
    }
    public void visit(WhileStatement statement){
        System.out.println("While");
    }

    public void visit(Function function){
        System.out.println("Function");
        Statement stmt = function.getBody();
        stmt.accept(this);
    }
    public void visit(Arguments arguments){
        System.out.println("Arguments");
    }
    public void visit(Parameters parameters){
        System.out.println("Parameters");
    }

    public void visit(ConversionExpression expression){
        System.out.println("ConvExpr");
    }
    public void visit(MulUnitExpression expression){
        System.out.println("MulUnitExpr");
    }
    public void visit(PowerUnitExpression expression){
        System.out.println("PowerUnitExpr");
    }
    public void visit(UnaryUnitExpression expression){
        System.out.println("UnaryUnitExpr");
    }
    public void visit(UnitExpressionLiteral<?> literal){
        System.out.println("UnitLiteral");
    }
    public void visit(UnitExpressionVariableValue variableValue){
        System.out.println("UnitVarVal");
    }

    public void visit(AndExpression expression){
        System.out.println("AndExpr");
    }
    public void visit(ArithmeticException expression){
        System.out.println("ArithmeticExpr");
    }
    public void visit(ComparisonExpression expression){
        System.out.println("CompExpr");
    }
    public void visit(MultiplyExpression expression){
        System.out.println("MultExpr");
    }
    public void visit(OrExpression expression){
        System.out.println("OrExpr");
    }
    public void visit(PowerExpression expression){
        System.out.println("PowerExpr");
    }
    public void visit(RelationalExpression expression){
        System.out.println("RelationalExpression");
    }
    public void visit(UnaryExpression expression){
        System.out.println("UnaryExpr");
    }

    @Override
    public void accept(Variable variable) {
        System.out.println("Variable");
    }
}
