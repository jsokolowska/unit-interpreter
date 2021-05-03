package util.tree.type;

public class UnitType extends Type {
    private String name;
    protected UnitType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals (Object obj){
        if(obj.getClass()!= this.getClass()){
            return false;
        }

        return ((UnitType) obj).getName().equals(this.getName());
    }
}
