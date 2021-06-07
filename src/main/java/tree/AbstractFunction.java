package tree;

import interpreter.Visitor;
import tree.function.Parameters;
import tree.type.Type;

public abstract class AbstractFunction {
    protected Parameters params;
    protected String name;

    public Parameters getParams() {
        return params;
    }
    public abstract void accept(Visitor visitor);

    public String getName(){
        return name;
    }

    public String getSignature(){
        var param_types = params.getParamMap().values();
        StringBuilder param_key = new StringBuilder();
        for (Type t : param_types){
            param_key.append(t.toString());
            param_key.append(",");
        }
        if(!param_key.isEmpty()){
            param_key.deleteCharAt(param_key.length()-1);
        }
        return name +"(" + param_key +")";
    }
}
