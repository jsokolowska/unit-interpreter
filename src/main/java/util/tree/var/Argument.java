package util.tree.var;



public class Argument {
    private String type;
    private String name;

    public Argument(String type, String  name) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName(){
        return name;
    }
}
