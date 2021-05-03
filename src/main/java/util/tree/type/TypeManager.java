package util.tree.type;

import exception.TypeException;
import util.Token;
import util.tree.unit.CompoundExpr;

import java.util.HashMap;
import java.util.Map;

public class TypeManager {

    private final Map<Token.TokenType, Type> baseTypes = new HashMap<>();   //for built in types and units
    private final Map<String, UnitType> units = new HashMap<>();            //for custom units

    public TypeManager (){
        initBaseTypes();
    }

    private void initBaseTypes(){
        baseTypes.put(Token.TokenType.TYPE_INT, new IntType());
        baseTypes.put(Token.TokenType.TYPE_FLOAT, new FloatType());
        baseTypes.put(Token.TokenType.TYPE_BOOL, new BoolType());
        baseTypes.put(Token.TokenType.TYPE_STRING, new StringType());

        baseTypes.put(Token.TokenType.TYPE_SEC, new UnitType("second"));
        baseTypes.put(Token.TokenType.TYPE_KG, new UnitType("kilogram"));
        baseTypes.put(Token.TokenType.TYPE_METER, new UnitType("meter"));
    }

    public Type getType(Token typeToken) {
        Type type = baseTypes.get(typeToken.getTokenType());
        if(type != null) return type;
        if(typeToken.getTokenType() != Token.TokenType.IDENTIFIER) return null;
        return units.get(typeToken.getStringValue());
    }

    /** @return UnitType if provided token matches unit type and has already been defined, null otherwise*/
    public UnitType getUnitType(Token typeToken){
        Token.TokenType type = typeToken.getTokenType();
        if (type == Token.TokenType.IDENTIFIER){
            return  units.get(typeToken.getStringValue());
        }
        if (typeToken.isBaseUnit()) return (UnitType) baseTypes.get(type);
        return null;
    }

    /** @return false if unit already exists and true if new one was added to the map */
    public void addUnit(Token unitToken) throws TypeException {
        if(unitToken.getTokenType()!= Token.TokenType.IDENTIFIER) throw new TypeException();
        String name = unitToken.getStringValue();
        units.put(name, new UnitType(name));

    }

    /** @return false if unit already exists and true if new one was added to the map */
    public void addUnit(CompoundType compound){
        units.put(compound.getName(), compound);
    }

    public boolean exists (String unitName){
        return units.containsKey(unitName);
    }

    public boolean exists (Token unitToken){
        if (unitToken.getTokenType()!= Token.TokenType.IDENTIFIER) return false;
        return units.containsKey(unitToken.getStringValue());
    }
}
