# unit-interpreter
Simple interpreter for language with units and unit conversions for TKOM 2021.

##Code examples
###unit definitions
```
unit kelvin;
unit celcius;
unit farenheit;
unit kilometer;

unit joule as <kilogram * meter^2 / second^2>;
unit newton as <kilogram * meter / second^2>;
```
###unit conversions
```
    let kiloMeter as (meter m) { 1000 * m };
    let kelvin (celsius c) { c + 273.15 };
    let farenheit (celsius c) { c * 9/5 + 32 };
    
    /*using conversions*/
    celsius c = 10;
    farenheit f = farenheit(c);    //= 77 F
    int temp =  f ;                // = 77, conversions to floating point or integer are built-in
```
### unit arithmetic
```
/* addition: possible if both expressions have the same type */
kelvin1 + kelvin2           //OK
kelvin1 + celsius           //WRONG, but
kelvin1 + kelvin(celsius)   //OK after conversion
10 + celsius                //WRONG, implicit conversion supported only for assignment

/* multiplication: supported for any numeric or unit types, might result in type change */
kelvin * 10         //Type: kelvin
celsius / kelvin    //Type: compound <celsius / kelvin>
celsius / celsius (kelvin)  //Type: float

joule * newton      //Type: <kilo^2 * meter^3 / second^4>
joule / newton      //Type: meter

meter * second      //Type: <meter * second>

/* exponentiation */
second^2        //OK
second^0.5      //WRONG, floating point exponents are not supported
second ^ meter  //WRONG, unit type cannot be exponent
```
