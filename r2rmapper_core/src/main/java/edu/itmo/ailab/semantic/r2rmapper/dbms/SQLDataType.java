package edu.itmo.ailab.semantic.r2rmapper.dbms;

/**
 * R2R Mapper. It is a free software.
 *
 * SLQDataTypes. Not all datatypes are suppported.
 * Author: Ilya Semerhanov
 * Date: 06.08.13
 */
public enum SQLDataType {

    INT,
    NUMERIC,
    DECIMAL,
    SMALLINT,
    INTEGER,
    BIGINT,
    FLOAT,
    DATE,
    TIME,
    TIMESTAMP,
    CHAR,
    VARCHAR,
    STRING;

    public static SQLDataType getTypeFromString(String type){
        switch(type){
            case "INT":
                return SQLDataType.INT;
            case "NUMERIC":
                return SQLDataType.NUMERIC;
            case "DECIMAL":
                return SQLDataType.DECIMAL;
            case "INTEGER":
                return SQLDataType.INTEGER;
            case "BIGINT":
                return SQLDataType.BIGINT;
            case "FLOAT":
                return SQLDataType.FLOAT;
            case "DATE":
                return SQLDataType.DATE;
            case "TIME":
                return SQLDataType.TIME;
            case "TIMESTAMP":
                return SQLDataType.TIMESTAMP;
            case "CHAR":
                return SQLDataType.CHAR;
            case "VARCHAR":
                return SQLDataType.VARCHAR;
            case "STRING":
                return SQLDataType.STRING;
            default:
                return SQLDataType.STRING;
        }
    }

}
