package interpreter;

import tree.Program;
import tree.Variable;
import tree.Visitable;
import tree.expression.math.*;
import tree.expression.operator.*;
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
    void visit(ConversionFunction conversionFunction);

    void visit(AssignStatement statement);
    void visit(BlockStatement statement);
    void visit(BreakStatement statement);
    void visit(CallStatement statement);
    void visit(ContinueStatement statement);
    void visit(IfElseStatement statement);
    void visit(PrintStatement statement);
    void visit(ReturnStatement statement);
    void visit(TypeStatement statement);
    void visit(VariableDeclarationStatement statement);
    void visit(WhileStatement statement);

    void visit(Function function);
    void visit(Parameters parameters);

    void visit(ConversionExpression expression);
    void visit(MulUnitExpression expression);
    void visit(PowerUnitExpression expression);
    void visit(UnaryUnitExpression expression);
    void visit(UnitExpressionLiteral<?> literal);
    void visit(UnitExpressionVariableValue variableValue);

    void visit(AndExpression expression);
    void visit(OrExpression expression);
    void visit(PowerExpression expression);
    void visit(UnaryExpression expression);

    void visit(ExpressionWithOperators expression);

    void visit(EqOperator operator);
    void visit(GreaterOperator operator);
    void visit(LessEqOperator operator);
    void visit(LessOperator operator);
    void visit(GreaterEqOperator operator);
    void visit(NotEqOperator operator);

    void visit(AndOperator operator);
    void visit(DivOperator operator);
    void visit(MinusOperator operator);
    void visit(MulOperator operator);
    void visit(NegOperator operator);
    void visit(NotOperator operator);
    void visit(OrOperator operator);
    void visit(PlusOperator operator);
    void visit(PowerOperator operator);
}
