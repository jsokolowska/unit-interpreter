package tree.type;

public class InferredCompound extends UnitType{
    protected InferredCompound() {
        super("");
    }

    @Override
    public String prettyToString() {
        return "unknown compound";
    }

    @Override
    public String toString() {
        return "inferred";
    }
}
