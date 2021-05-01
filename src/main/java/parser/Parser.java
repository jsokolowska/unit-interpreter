package parser;

import exception.ParserException;
import org.jetbrains.annotations.VisibleForTesting;
import scanner.Scanner;
import util.Token;
import util.Token.TokenType;
import util.tree.Expression;
import util.tree.Function;
import util.tree.Program;
import util.tree.statement.BlockStatement;
import util.tree.statement.ReturnStatement;
import util.tree.statement.Statement;
import util.tree.unit.Conversion;
import util.tree.unit.UnitDeclaration;
import util.tree.unit.compound.CompoundPart;
import util.tree.unit.compound.CompoundExpr;

import java.io.IOException;

public class Parser {
    private final Scanner scanner;
    private Token token;

    public Parser (Scanner scanner){
        this.scanner = scanner;
    }

    public Program parse() throws IOException {
        Program program = new Program();
        token = scanner.getToken();

        while(token.getTokenType() == TokenType.UNIT || token.getTokenType() == TokenType.LET){
            if(token.getTokenType() == TokenType.UNIT){
                    UnitDeclaration u = parseUnitDeclaration();
                    program.add(u);
            }else{      // case LET
                    Conversion c = parseUnitConversion();
                    program.add(c);
            }
        }
        while (isType(token)){
            Function fun = parseFunction();
            program.add(fun);
        }

        // handle unexpected tokens
        if (token.getTokenType()!= TokenType.EOT){
            throw new ParserException("function definition", token.getTokenType(), token.getPosition());
        }

        return program;
    }

    private static boolean isType(Token token){
        return token.getTokenType() == TokenType.BASE_TYPE
                || token.getTokenType() == TokenType.BASE_UNIT
                || token.getTokenType() == TokenType.IDENTIFIER;
    }

    private UnitDeclaration parseUnitDeclaration() throws IOException {
        token = scanner.getToken();
        if(token.getTokenType() == TokenType.IDENTIFIER){
            String unitName = token.getStringValue();
            token = scanner.getToken();
            CompoundExpr type = null;
            if(token.getTokenType() == TokenType.AS){
                type = parseCompoundType();
            }
            if(token.getTokenType() == TokenType.SEMICOLON){
                return  new UnitDeclaration(unitName, type);
            }else{
                throw new ParserException(TokenType.SEMICOLON, token.getTokenType(), token.getPosition());
            }
        }
        throw new ParserException(TokenType.IDENTIFIER, token.getTokenType(), token.getPosition());
    }

    private CompoundExpr parseCompoundType() throws IOException{
        token = scanner.getToken();


        return null;
    }
    private CompoundPart parseCompoundPart(){

        return null;
    }

    private Conversion parseUnitConversion() {
        return new Conversion();
    }

    private Function parseFunction(){
        return  new Function();
    }

    @VisibleForTesting
    private Statement parseStatements() throws IOException {
        token = scanner.getToken();
        if (token.getTokenType()==TokenType.CURLY_OPEN){
            return parseBlockStatement();
        }else{
            return parseOneStatement();
        }
    }
    private Statement parseOneStatement() throws IOException{
        if (token.getTokenType()==TokenType.RETURN){
            return parseReturnStatement();
        }
        return null;
    }
    private BlockStatement parseBlockStatement() throws IOException{
        return null;
    }
    private ReturnStatement parseReturnStatement() throws IOException{
        token = scanner.getToken();
        Expression expr = null;

        //build value if exists
        if(token.getTokenType() != TokenType.SEMICOLON){
            expr = parseExpression();
        }
        return new ReturnStatement(expr);

    }
    private Expression parseExpression () throws IOException{
        return null;
    }




}
