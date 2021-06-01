package interpreter;

import interpreter.env.Environment;
import interpreter.util.Casting;
import interpreter.util.StackValue;
import tree.AbstractFunction;
import tree.Program;
import tree.Variable;
import tree.Visitable;
import tree.expression.Expression;
import tree.expression.math.*;
import tree.expression.operator.*;
import tree.expression.operator.unit.*;
import tree.expression.unit.*;
import tree.expression.unit.value.UnitExpressionLiteral;
import tree.expression.unit.value.UnitExpressionVariableValue;
import tree.function.Function;
import tree.function.Parameters;
import tree.statement.*;
import tree.type.*;
import tree.unit.*;
import tree.value.FunctionCall;
import tree.value.Literal;
import tree.value.VariableValue;
import util.exception.InterpretingException;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class Interpreter implements Visitor{
    private final Program program;
    private final Environment env;
    private Integer line;
    private final Casting casting;
    private final PrintStream printStream;

    public Interpreter(Program program, Environment env, PrintStream printStream) throws IOException {
        this.program = program;
        this.env = env;
        line = 0;
        this.casting = new Casting(line);
        this.printStream = printStream;
    }

    public Interpreter(Program program, Environment env) throws IOException {
        this.program = program;
        this.env = env;
        line = 0;
        this.casting = new Casting(line);
        printStream = System.out;
    }

    public void execute() {
        program.accept(this);
    }

    public void visit(Visitable visitable){
        throw new RuntimeException("Unrecognized Visitable");
    }

    public void visit(Program program){
        Function main = program.getFunction("main");
        if(main == null){
            throw new InterpretingException("Program must contain function main");
        }
        env.pushNewCallScope();
        main.accept(this);
        env.popCallScope();
    }

    public void visit(Variable var){
        if(env.variableExistsInBlock(var.getIdentifier()))
            throw new InterpretingException("Variable redefinition " + var.getIdentifier(), line);

        env.addVariable(var);
    }

    public void visit(FunctionCall functionCall){
        String funName = functionCall.getIdentifier();

        AbstractFunction function = program.getFunctionOrConversionFunction(funName);
        if(function == null){
            visitProvidedConversion(functionCall);
        }else{
            visitFunction(functionCall, function);
        }
    }

    public void visitFunction(FunctionCall functionCall, AbstractFunction fun){
        Parameters params = fun.getParams();
        List<Expression> args = functionCall.getArgs().getArgList();
        if(params.size()!= args.size()){
            throw new InterpretingException("Wrong number of arguments passed to function call or conversion call", line);
        }
        for (var a : args){
            a.accept(this);
        }
        env.pushNewCallScope();
        fun.accept(this);
        //get return value out of call scope
        if(!env.isStackEmpty()){
            var temp = env.popValue();
            env.popCallScope();
            env.pushValue(temp);
        }else{
            env.popCallScope();
        }
        env.setReturned(false);
        env.setReturnedWithValue(false);
        env.setContinued(false);
        env.setBroken(false);
    }

    public void visitProvidedConversion(FunctionCall functionCall){
        String funName = functionCall.getIdentifier();
        Type t;
        if(funName.equals("int")){
            t = new IntType();
        }else if(funName.equals("float")){
            t = new DoubleType();
        }else{
            throw new InterpretingException("Unknown function or conversion identifier " + funName, line);
        }

        var args = functionCall.getArgs().getArgList();;
        if(args.size() != 1){
            throw new InterpretingException("Wrong number of arguments passed to buit-in conversion call", line);
        }

        //push provided expression to the stack
        args.get(0).accept(this);
        var result = env.popValue();
        var converted = casting.cast(result, t);
        env.pushValue(converted);
    }

    public void visit(Function function){
        function.getParams().accept(this);

        //todo is void type possible? What if there is a return instruction but without value
        setParameterValues(function.getParams());

        Statement stmt = function.getBody();
        stmt.accept(this);
        if(env.hasReturned()){
            if(env.hasReturnedWithValue()){
                if(function.getReturnType() == null){
                    throw new InterpretingException("Return value for function that returns void", line);
                }
                env.pushValue(casting.cast(env.popValue(), function.getReturnType()));
            }else if(function.getReturnType() != null){
                throw new InterpretingException("Empty return statement for non-void function", line);
            }
        }else{
            if(function.getReturnType() != null){
                throw new InterpretingException("Expected return statement ", line);
            }
        }
    }

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
        StackValue stackValue = new StackValue(variable);
        env.pushValue(stackValue);
    }

    public void visit(ConversionFunction conversionFunction){
        conversionFunction.getParameters().accept(this);
        setParameterValues(conversionFunction.getParameters());
        conversionFunction.getConversionExpression().accept(this);
        var result = env.popValue();
        env.pushValue(casting.cast(result, conversionFunction.getResultType()));
    }

    private void setParameterValues(Parameters parameters){
        var paramMap = parameters.getParameters();
        var it = paramMap.entrySet().stream().toList().listIterator(paramMap.size());
        while(it.hasPrevious()){
            var current_param = it.previous();
            var cast_value = casting.cast(env.popValue(), current_param.getValue());
            Variable var = env.getVariable(current_param.getKey());
            var.setValue(cast_value.getValueAsLiteral());
        }
    }

    public void visit(AssignStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        String id = statement.getIdentifier();
        Variable var = env.getVariable(id);
        if(var == null){
            throw new InterpretingException("Variable does not exist " + id, line);
        }
        var expr = statement.getAssignExpression();
        expr.accept(this);
        StackValue val = env.popValue();

        StackValue castResult = casting.cast(val, var.getType());
        var.setValue(castResult.getValueAsLiteral());
    }

    public void visit(BlockStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        env.pushNewBlock();
        for (var stmt : statement.getStatements()){
            stmt.accept(this);
        }
        env.popBlock();
    }

    public void visit(BreakStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        env.setBroken(true);
    }

    public void visit(CallStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        statement.getFunCall().accept(this);
    }

    public void visit(ContinueStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        env.setContinued(true);
    }

    public void visit(IfElseStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();

        statement.getIfCondition().accept(this);
        boolean conditionValue = (boolean) casting.cast(env.popValue(), new BoolType()).getValue();
        if(conditionValue){
            statement.getIfStatement().accept(this);
        }else{
            Statement elseStatement = statement.getElseStatement();
            if(elseStatement != null){
                elseStatement.accept(this);
            }
        }
    }

    public void visit(PrintStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        var arg_list = statement.getArguments().getArgList();
        StringBuilder str = new StringBuilder();

        for(var a : arg_list){
            a.accept(this);
            String val = (String) casting.cast(env.popValue(), new StringType()).getValue();
            str.append(val).append(", ");
        }
        str.deleteCharAt(str.length()-1);
        str.deleteCharAt(str.length()-1);
        printStream.println(str);
    }

    public void visit(ReturnStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();

        Expression retExpr = statement.getReturnExpression();
        if(retExpr!=null){
            retExpr.accept(this);
            env.setReturnedWithValue(true);
        }
        env.setReturned(true);
    }

    public void visit(TypeStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        Expression ex = statement.getExpression();

        ex.accept(this);

        Type t = env.popValue().getType();
        printStream.println(t.prettyToString());
    }

    public void visit(VariableDeclarationStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();

        statement.getVariable().accept(this);
        AssignStatement stmt = statement.getAssignStatement();
        if(stmt!=null){
            stmt.accept(this);
        }
    }

    public void visit(WhileStatement statement){
        if(env.hasReturned()) return;
        env.setBroken(false);
        env.setContinued(false);
        line = statement.getLine();

        Expression condition = statement.getCondition();
        Statement body = statement.getBody();

        condition.accept(this);
        boolean conditionValue = (boolean) casting.cast(env.popValue(), new BoolType()).getValue();
        while(conditionValue && !env.hasBroken() && !env.hasReturned()){
            body.accept(this);
            condition.accept(this);
            conditionValue = (boolean) casting.cast(env.popValue(), new BoolType()).getValue();
            env.setContinued(false);
        }
        env.setBroken(false);
        env.setContinued(false);
    }

    public void visit(ConversionExpression expression){
        var expressions = expression.getExpressions();
        var operators = expression.getOperators();
        int size = expression.size()-1;
        expressions.get(0).accept(this);
        for(int i=0; i< size; i++){
            expressions.get(i+1).accept(this);
            operators.get(i).accept(this);
        }
    }

    public void visit(MulUnitExpression expression){
        var expressions = expression.getExpressions();
        var operators = expression.getOperators();
        int size = expression.size()-1;
        expressions.get(0).accept(this);

        for (int i=0; i<size; i++){
            expressions.get(i+1).accept(this);
            operators.get(i).accept(this);
        }
    }

    public void visit(PowerUnitExpression expression){
        var expressions = expression.getExpressions();
        int size = expression.size();
        for(var e: expressions){
            e.accept(this);
        }
        for (int i=0; i< size-1; i++){
            new UnitPowerOperator().accept(this);
        }
    }

    public void visit(UnaryUnitExpression expression){
        expression.getExpr().accept(this);
        new UnitNegOperator().accept(this);
    }

    public void visit(UnitExpressionLiteral<?> literal){
        env.pushValue(new Literal<>(literal.getValue()));
    }

    public void visit(UnitExpressionVariableValue variableValue){
        Variable known = env.getVariable(variableValue.getIdentifier());
        if(known == null){
            throw new InterpretingException("Unknown identifier", line);
        }
        env.pushValue(new StackValue(known));
    }

    public void visit(UnitNegOperator operator){
        var obj = env.popValue();
        var val = obj.getValue();
        if(val instanceof Integer iVal){
            env.pushValue(new Literal<>(-iVal));
        }else if(val instanceof Double dVal){
            env.pushValue(new Literal<>(-dVal));

        }else{
            throw new InterpretingException("Cannot negate " + obj.getType().prettyToString());
        }
    }

    public void visit(UnitDivOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        if(lObj.getValue() instanceof Number lNum && rObj.getValue() instanceof Number rNum){
            var res = casting.divideWithValueCast(lNum, rNum);
            env.pushValue(new Literal<>(res));
        }else{
            throw new InterpretingException("Cannot multiply " + lObj.getType().prettyToString() + " and "
                    + rObj.getType().prettyToString());
        }
    }

    public void visit(UnitMinusOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        if(lObj.getValue() instanceof Number lNum && rObj.getValue() instanceof Number rNum){
            var res = casting.subtractionWithValueCast(lNum, rNum);
            env.pushValue(new Literal<>(res));
        }else{
            throw new InterpretingException("Cannot multiply " + lObj.getType().prettyToString() + " and "
                    + rObj.getType().prettyToString());
        }
    }

    public void visit(UnitMulOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        if(lObj.getValue() instanceof Number lNum && rObj.getValue() instanceof Number rNum){
            var res = casting.multiplyWithValueCast(lNum, rNum);
            env.pushValue(new Literal<>(res));
        }else{
            throw new InterpretingException("Cannot multiply " + lObj.getType().prettyToString() + " and "
                    + rObj.getType().prettyToString());
        }
    }

    public void visit(UnitPlusOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        if(lObj.getValue() instanceof Number lNum && rObj.getValue() instanceof Number rNum){
            var res = casting.additionWithValueCast(lNum, rNum);
            env.pushValue(new Literal<>(res));
        }else{
            throw new InterpretingException("Cannot multiply " + lObj.getType().prettyToString() + " and "
                    + rObj.getType().prettyToString());
        }
    }

    public void visit(UnitPowerOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();

        if(lObj.getValue() instanceof Number lNum && rObj.getValue() instanceof Number rNum){
            var resultValue = casting.exponentiateWithValueCast(lNum, rNum);
            env.pushValue(new Literal<>(resultValue));
        }else{
            throw new InterpretingException("Expected numericValue, got" + rObj.getType().prettyToString() +" and "
                    + lObj.getType().prettyToString());
        }
    }

    public void visit(AndExpression expression){
        List<Expression> expressions = expression.getExpressions();
        boolean value = true;
        for(Expression expr : expressions){
            expr.accept(this);
            boolean temp = (boolean) casting.cast(env.popValue(), new BoolType()).getValue();
            value = value && temp;
        }
        env.pushValue(new Literal<>(value));
    }

    public void visit(EqOperator operator){
        var rValue = env.popValue().getValue();
        var lValue = env.popValue().getValue();
        env.pushValue(new Literal<>(lValue.equals(rValue)), new BoolType());
    }

    public void visit(GreaterOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        env.pushValue(new Literal<>(casting.compareToWithCast(lObj, rObj) > 0), new BoolType());
    }

    public void visit(LessEqOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        env.pushValue(new Literal<>(casting.compareToWithCast(lObj, rObj) <= 0), new BoolType());
    }

    public void visit(LessOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        env.pushValue(new Literal<>(casting.compareToWithCast(lObj, rObj) < 0), new BoolType());
    }

    public void visit(GreaterEqOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        env.pushValue(new Literal<>(casting.compareToWithCast(lObj, rObj) >= 0), new BoolType());
    }

    public void visit(NotEqOperator operator) {
        var first = env.popValue().getValue();
        var second = env.popValue().getValue();
        env.pushValue(new Literal<>(first != second), new BoolType());
    }

    public void visit(AndOperator operator){
        //no need for vistor to visit this node
    }

    public void visit(DivOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();

        Type resultType = casting.calculateTypeForDivision(lObj.getType(), rObj.getType());
        Number resultVal = casting.divideWithValueCast((Number) lObj.getValue(), (Number) rObj.getValue());
        env.pushValue(new Literal<>(resultVal), resultType);
    }

    public void visit(MinusOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        var lType = lObj.getType();
        var rType = rObj.getType();

        if(rType.equals(lType) && (rType instanceof NumericType || rType instanceof UnitType)){
            var rVal = (Number) rObj.getValue();
            var lVal = (Number) lObj.getValue();
            var result = casting.subtractionWithValueCast(lVal, rVal);
            env.pushValue(new Literal<>(result), lType);
        }else if(rType instanceof NumericType && lType instanceof NumericType){
            var rVal = (Number) rObj.getValue();
            var lVal = (Number) lObj.getValue();
            var result = casting.subtractionWithValueCast(lVal, rVal);
            env.pushValue(new Literal<>(result), new DoubleType());
        }else{
            throw new InterpretingException("Cannot apply substraction to: " + lObj.getType()
                    + " and " + rObj.getType(), line);
        }
    }

    public void visit(PlusOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        var lVal = lObj.getValue();
        var rVal = rObj.getValue();
        var lType = lObj.getType();
        var rType = rObj.getType();

        if(lType.equals(rType)){
            var result = casting.additionWithValueCast( lVal, rVal);
            env.pushValue(new Literal<>(result), lType);
        }else if(lType instanceof NumericType && rType instanceof NumericType){
            var result = casting.additionWithValueCast(lVal, rVal);
            env.pushValue(new Literal<>(result), lType);
        }else{
            throw new InterpretingException("Cannot apply substraction to: " + lObj.getType()
                    + " and " + rObj.getType(), line);
        }
    }

    public void visit(MulOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();

        if(rObj.getValue() instanceof Number rNum && lObj.getValue() instanceof Number lNum){
            var resultVal = casting.multiplyWithValueCast(rNum, lNum);
            Type resultType = casting.calculateTypeForMultiplication(rObj.getType(), lObj.getType());
            env.pushValue(new Literal<>(resultVal), resultType);
        }else{
            throw new InterpretingException("Cannot apply division to: " + lObj.getType()
                    + " and " + rObj.getType(), line);
        }
    }

    public void visit(NegOperator operator){
        var obj = env.popValue();
        var value = obj.getValue();
        if(value instanceof Integer intVal){
            env.pushValue(new Literal<>(-intVal), obj.getType());
        }else if(value instanceof Double dVal){
            env.pushValue(new Literal<>(-dVal), obj.getType());
        }else{
            throw new InterpretingException("Cannot negate " + obj.getType().prettyToString(), line);
        }
    }

    public void visit(NotOperator operator){
        var obj = env.popValue();
        boolean value  = (Boolean) casting.cast(obj, new BoolType()).getValue();
        env.pushValue(new Literal<>(!value), new BoolType());
    }

    public void visit(OrOperator operator){
        //no need for vistor to visit this node
    }

    public void visit(PowerOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        var rValue = rObj.getValue();
        var lValue = lObj.getValue();

        var resultType = casting.calculateTypeForExponentiation(lObj, rObj);
        var resultValue = casting.exponentiateWithValueCast((Number)lValue, (Number) rValue);
        env.pushValue(new Literal<>(resultValue), resultType);
    }


    public void visit(ExpressionWithOperators expression){
        List<Expression> expressions = expression.getExpressions();
        List<Operator> operators = expression.getOperators();
        expressions.get(0).accept(this);
        for(int i =0; i<operators.size(); i++){
            expressions.get(i+1).accept(this);
            operators.get(i).accept(this);
        }
    }

    public void visit(OrExpression expression){
        List<Expression> expressions = expression.getExpressions();
        boolean value = false;
        for(Expression expr : expressions){
            expr.accept(this);
            boolean temp = (Boolean) casting.cast(env.popValue(), new BoolType()).getValue();
            value = value || temp;
        }
        env.pushValue(new Literal<>(value));
    }

    public void visit(PowerExpression expression){
        List<Expression> expressions = expression.getExpressions();
        int size = expression.size();
        for(Expression expr : expressions){
            expr.accept(this);
        }
        PowerOperator powerOperator = new PowerOperator();
        for(int i=0; i<size-1; i++){
            powerOperator.accept(this);
        }
    }

    public void visit(UnaryExpression expression){
        Expression part = expression.getExpr();
        Operator op = expression.getOp();
        part.accept(this);
        op.accept(this);
    }
}
