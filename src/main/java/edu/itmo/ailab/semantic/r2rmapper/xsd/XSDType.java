package edu.itmo.ailab.semantic.r2rmapper.xsd;

/**
 * R2R Mapper. XSD Types.
 * User: Ilya Semerhanov
 * Date: 06.08.13
 *
 */
public enum XSDType {

    INTEGER("integer"),
    STRING("string"),
    INT("int"),
    LONG("long"),
    FLOAT("float"),
    DECIMAL("decimal"),
    DATE("date"),
    DATETIME("dateTime"),
    TIME("time");

    private String type;
    public static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";

    private XSDType(String type){
        this.type = type;
    }

    public String toString(){
        return type;
    }

}
