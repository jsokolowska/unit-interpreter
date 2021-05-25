package interpreter;

import tree.Program;
import tree.Variable;
import tree.Visitable;
import tree.expression.math.*;
import tree.expression.unit.*;
import tree.expression.unit.value.UnitExpressionLiteral;
import tree.expression.unit.value.UnitExpressionVariableValue;
import tree.function.*;
import tree.statement.*;
import tree.unit.*;
import tree.value.*;

public interface Visitor {
    void visit(Visitable visitable);
    void visit(Program program);
    void visit(Variable var);
    void visit(FunctionCall functionCall);

    void visit(Literal<?> literal);



    void visit(VariableValue variableValue);
    void visit(CompoundExpr expr);
    void visit(CompoundTerm term);
    void visit(Conversion conversion);
    void visit(UnitDeclaration unitDeclaration);
    void visit(UnitParameters parameters);

    void visit(AssignStatement statement);
    void visit(BlockStatement statement);
    void visit(BreakStatement statement);
    void visit(CallStatement statement);
    void visit(ContinueStatement statement);
    void visit(ExplainStatement statement);
    void visit(IfElseStatement statement);
    void visit(PrintStatement statement);
    void visit(ReturnStatement statement);
    void visit(TypeStatement statement);
    void visit(VariableDeclarationStatement statement);
    void visit(WhileStatement statement);

    void visit(Function function);
    void visit(Arguments arguments);
    void visit(Parameters parameters);

    void visit(ConversionExpression expression);
    void visit(MulUnitExpression expression);
    void visit(PowerUnitExpression expression);
    void visit(UnaryUnitExpression expression);
    void visit(UnitExpressionLiteral<?> literal);
    void visit(UnitExpressionVariableValue variableValue);

    void visit(AndExpression expression);
    void visit(ArithmeticException expression);
    void visit(ComparisonExpression expression);
    void visit(MultiplyExpression expression);
    void visit(OrExpression expression);
    void visit(PowerExpression expression);
    void visit(RelationalExpression expression);
    void visit(UnaryExpression expression);
}
