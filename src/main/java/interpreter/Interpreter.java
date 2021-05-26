package interpreter;

import interpreter.env.Environment;
import interpreter.util.StackValue;
import tree.AbstractFunction;
import tree.Program;
import tree.Variable;
import tree.Visitable;
import tree.expression.Expression;
import tree.expression.math.*;
import tree.expression.operator.*;
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
import util.exception.CastingException;
import util.exception.InterpretingException;

import java.io.IOException;
import java.util.List;

public class Interpreter implements Visitor{
    private final Program program;
    private final TypeManager typeManager;
    private final Environment env;
    private Integer line;
    private final Casting casting;

    public Interpreter(Program program, TypeManager typeManager, Environment env) throws IOException {
        this.program = program;
        this.typeManager = typeManager;
        this.env = env;
        line = 0;
        this.casting = new Casting(line);
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

    //todo tests
    public void visit(VariableValue variableValue){
        Variable variable = env.getVariable(variableValue.getIdentifier());
        if(variable == null){
            throw new InterpretingException("Unknown identifier: " + variableValue.getIdentifier(), line);
        }
        StackValue stackValue = new StackValue(variable);
        env.pushValue(stackValue);
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
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        //todo
        System.out.println("Assign");
    }
    public void visit(BlockStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        //todo tests
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
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        env.setBroken(true);
    }
    public void visit(CallStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        //todo
        System.out.println("CallSTMT");
    }
    public void visit(ContinueStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        env.setContinued(true);
        //todo
        System.out.println("ContinueStmt");
    }
    public void visit(ExplainStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        //todo
        System.out.println("ExplainStmt");
    }
    public void visit(IfElseStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        //todo
        System.out.println("Ifelse");
    }
    public void visit(PrintStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        //todo
        System.out.println("Print");
    }
    public void visit(ReturnStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        //todo
        System.out.println("Return");
    }
    public void visit(TypeStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        //todo
        String id = statement.getIdentifier();

        System.out.println();
    }
    public void visit(VariableDeclarationStatement statement){
        if(env.hasBroken() || env.hasContinued() || env.hasReturned()) return;
        line = statement.getLine();
        //todo
        System.out.println("Vardcl");
    }
    public void visit(WhileStatement statement){
        if(env.hasReturned()) return;
        env.setBroken(false);
        env.setContinued(false);
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
        List<Expression> expressions = expression.getExpressions();
        boolean value = true;
        for(Expression expr : expressions){
            expr.accept(this);
            boolean temp = casting.castToBoolean(env.popValue());
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
        env.pushValue(new Literal<>(compareToWithCast(lObj, rObj) > 0), new BoolType());
    }

    private int compareToWithCast(StackValue left, StackValue right){
        var rValue = right.getValue().getLiteralValue();
        var lValue = left.getValue().getLiteralValue();
        if(rValue instanceof Integer rNum && lValue instanceof Integer lNum){
            return lNum.compareTo(rNum);
        }else if(rValue instanceof Double rDouble && lValue instanceof Double lDouble){
            return lDouble.compareTo(rDouble);
        }else if(rValue instanceof Double rNum && lValue instanceof Integer lNum) {
            return - rNum.compareTo(Double.valueOf(lNum));
        }else if(rValue instanceof Integer rNum && lValue instanceof Double lNum) {
            return lNum.compareTo(Double.valueOf(rNum));
        }else {
            throw new InterpretingException("Cannot compare " + left.getType() + " and " + right.getType(), line );
        }
    }

    public void visit(LessEqOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        env.pushValue(new Literal<>(compareToWithCast(lObj, rObj) <= 0), new BoolType());
    }

    public void visit(LessOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        env.pushValue(new Literal<>(compareToWithCast(lObj, rObj) < 0), new BoolType());
    }

    public void visit(GreaterEqOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        env.pushValue(new Literal<>(compareToWithCast(lObj, rObj) >= 0), new BoolType());
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
        var rVal = rObj.getValue().getLiteralValue();
        var lVal = lObj.getValue().getLiteralValue();
        Type resultType = casting.calculateTypeForDivision(lObj.getType(), rObj.getType());
        if(rVal instanceof Number rNum && lVal instanceof Number lNum){
            if (rNum.equals(0)){
                throw new InterpretingException("Division by zero", line);
            }
            Number resultVal;
            if(rVal instanceof Integer rInt && lVal instanceof  Integer lInt){
                if(lInt % rInt == 0){
                    resultVal = lInt/rInt;
                }else{
                    resultVal = Double.valueOf(lInt)/Double.valueOf(rInt);
                }
            }else if(rVal instanceof Integer rInt && lVal instanceof  Double lDb){
                resultVal = lDb/rInt;
            }else if(rVal instanceof Double rDb && lVal instanceof  Double lDb){
                resultVal = lDb/rDb;
            }else if(rVal instanceof Double rDb && lVal instanceof  Integer lInt){
                resultVal = lInt/rDb;
            }else{
                throw new InterpretingException("Unrecognized numeric value", line);
            }
            env.pushValue(new Literal<>(resultVal), resultType);
        }else{
            throw new InterpretingException("Cannot apply division to: " + lObj.getType()
                                            + " and " + rObj.getType(), line);
        }
    }

    public void visit(MinusOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        var rVal = rObj.getValue().getLiteralValue();
        var lVal = lObj.getValue().getLiteralValue();

        if(lVal instanceof Number && rVal instanceof Number){
            Type resultType = Casting.calculateTypeForAdditiveOperation(rObj.getType(), lObj.getType());
            if(rObj.getType() instanceof IntType || rObj.getType() instanceof FloatType){
                //simple numeric
                Number lNum = (Number) lVal;
                Number resultVal = 0;
                if(rVal instanceof Integer rInt){
                    resultVal = doAddition(lNum, -rInt);
                }else if(rVal instanceof Double rDb){
                    resultVal = doAddition(lNum, -rDb);
                }
                env.pushValue(new Literal<>(resultVal), resultType);
            }else if(rObj.getType().equals(lObj.getType())){
                //unit numeric
                Number lNum = (Number) lVal;
                Number resultVal = 0;
                if(rVal instanceof Integer rInt){
                    resultVal = doAddition(lNum, -rInt);
                }else if(rVal instanceof Double rDb){
                    resultVal = doAddition(lNum, -rDb);
                }
                env.pushValue(new Literal<>(resultVal), resultType);
            }
        }else{
            throw new InterpretingException("Cannot apply addition to: " + lObj.getType()
                    + " and " + rObj.getType(), line);
        }
    }

    public void visit(PlusOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        var rVal = rObj.getValue().getLiteralValue();
        var lVal = lObj.getValue().getLiteralValue();

        if(lVal instanceof Number && rVal instanceof Number){
            Type resultType = Casting.calculateTypeForAdditiveOperation(rObj.getType(), lObj.getType());
            if(rObj.getType() instanceof IntType || rObj.getType() instanceof FloatType){
                //simple numeric
                Number lNum = (Number) lVal;
                Number rNum = (Number) rVal;
                Number resultVal = doAddition(lNum, rNum);
                env.pushValue(new Literal<>(resultVal), resultType);
            }else if(rObj.getType().equals(lObj.getType())){
                //unit numeric
                Number lNum = (Number) lVal;
                Number rNum = (Number) rVal;
                Number resultVal = doAddition(lNum, rNum);
                env.pushValue(new Literal<>(resultVal), resultType);
            }
        }else{
            throw new InterpretingException("Cannot apply substraction to: " + lObj.getType()
                    + " and " + rObj.getType(), line);
        }
    }

    private Number doAddition(Number one, Number two){
        if(one instanceof Integer rInt && two instanceof  Integer lInt){
            return lInt + rInt;
        }else if(one instanceof Integer rInt && two instanceof  Double lDb){
            return lDb + rInt;
        }else if(one instanceof Double rDb && two instanceof  Double lDb){
            return lDb + rDb;
        }else if(one instanceof Double rDb && two instanceof  Integer lInt){
            return lInt + rDb;
        }else{
            throw new InterpretingException("Unrecognized numeric value", line);
        }
    }

    public void visit(MulOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        var rVal = rObj.getValue().getLiteralValue();
        var lVal = lObj.getValue().getLiteralValue();
        Type resultType = casting.calculateTypeForDivision(lObj.getType(), rObj.getType());
        if(rVal instanceof Number rNum && lVal instanceof Number){
            if (rNum.equals(0)){
                throw new InterpretingException("Division by zero", line);
            }
            Number resultVal;
            if(rVal instanceof Integer rInt && lVal instanceof  Integer lInt){
                resultVal = lInt * rInt;
            }else if(rVal instanceof Integer rInt && lVal instanceof  Double lDb){
                resultVal = lDb * rInt;
            }else if(rVal instanceof Double rDb && lVal instanceof  Double lDb){
                resultVal = lDb * rDb;
            }else if(rVal instanceof Double rDb && lVal instanceof  Integer lInt){
                resultVal = lInt * rDb;
            }else{
                throw new InterpretingException("Unrecognized numeric value", line);
            }
            env.pushValue(new Literal<>(resultVal), resultType);
        }else{
            throw new InterpretingException("Cannot apply division to: " + lObj.getType()
                    + " and " + rObj.getType(), line);
        }
    }

    public void visit(NegOperator operator){
        var obj = env.popValue();
        var value = obj.getValue().getLiteralValue();
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
        boolean value  = casting.castToBoolean(obj);
        env.pushValue(new Literal<>(!value), new BoolType());
    }

    public void visit(OrOperator operator){
        //no need for vistor to visit this node
    }

    public void visit(PowerOperator operator){
        var rObj = env.popValue();
        var lObj = env.popValue();
        var rValue = rObj.getValue().getLiteralValue();
        var lValue = lObj.getValue().getLiteralValue();
        if(rValue instanceof Integer rInt && rObj.getType() instanceof IntType){
            if(lValue instanceof Integer lInt){
                env.pushValue( new Literal<>(Math.pow(lInt, rInt)), lObj.getType());
            }else if(lValue instanceof Double lDb){
                env.pushValue(new Literal<>(Math.pow(lDb, rInt)), lObj.getType());
            }
        }else{
            throw new InterpretingException("Exponent is not an integer but " + rObj.getType().prettyToString(), line);
        }
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
            boolean temp = casting.castToBoolean(env.popValue());
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
