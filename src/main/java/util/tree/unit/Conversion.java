package util.tree.unit;

import util.tree.Node;
import util.tree.function.Parameters;
import util.tree.type.UnitType;

public class Conversion implements Node {
    private UnitType to;
    private Parameters parameters;
    private ConversionFunction conversionFunction;

    public Conversion (UnitType to, Parameters parameters, ConversionFunction conversionFunction){
        this.conversionFunction = conversionFunction;
        this.to = to;
        this.parameters = parameters;
    }
}
