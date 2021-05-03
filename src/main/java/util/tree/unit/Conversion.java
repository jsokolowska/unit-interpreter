package util.tree.unit;

import util.tree.Node;
import util.tree.function.Parameters;

public class Conversion implements Node {
    private String name;
    private Parameters parameters;
    private ConversionFunction conversionFunction;

    public Conversion (String name, Parameters parameters, ConversionFunction conversionFunction){
        this.conversionFunction = conversionFunction;
        this.name = name;
        this.parameters = parameters;
    }
}
