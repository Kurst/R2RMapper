package edu.itmo.ailab.semantic.r2rmapper.xsd;

import edu.itmo.ailab.semantic.r2rmapper.dbms.SQLDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * R2R Mapper. Mapping between SQLDataTypes and XSDTypes
 * Not all types are supported.
 * User: Ilya Semerhanov
 * Date: 06.08.13
 *
 */
public class XSDMapping {

    private static Map<SQLDataType, XSDType> typesMapping = new HashMap<>();

    static {
        typesMapping.put(SQLDataType.NUMERIC, XSDType.DECIMAL);
        typesMapping.put(SQLDataType.DECIMAL, XSDType.DECIMAL);
        typesMapping.put(SQLDataType.SMALLINT, XSDType.INT);
        typesMapping.put(SQLDataType.INTEGER, XSDType.INTEGER);
        typesMapping.put(SQLDataType.INT, XSDType.INT);
        typesMapping.put(SQLDataType.BIGINT, XSDType.LONG);
        typesMapping.put(SQLDataType.FLOAT, XSDType.FLOAT);
        typesMapping.put(SQLDataType.DATE, XSDType.DATE);
        typesMapping.put(SQLDataType.TIME, XSDType.TIME);
        typesMapping.put(SQLDataType.TIMESTAMP, XSDType.DATETIME);
        typesMapping.put(SQLDataType.VARCHAR, XSDType.STRING);
        typesMapping.put(SQLDataType.CHAR, XSDType.STRING);
        typesMapping.put(SQLDataType.STRING, XSDType.STRING);
    }
    public static XSDType getXSDType(SQLDataType type){
        return typesMapping.get(type);
    }

}
