package edu.itmo.ailab.semantic.r2rmapper.dbms;


/**
 * R2R Mapper. It is a free software.
 *
 * Drivers for RDBMS.
 * Author: Ilya Semerhanov
 * Date: 06.08.13
 */
public class Driver {
	public static Driver MysqlDriver = new Driver("com.mysql.jdbc.Driver");
    public static Driver PostgreSQLDriver = new Driver("org.postgresql.Driver");
    public static Driver OracleSQLDriver = new Driver("oracle.jdbc.driver.OracleDriver");

    private String driverName;
    
    public Driver(String driverName) {
    	this.driverName=  driverName;
    }
    
    public String getDriverName() {
    	return driverName;
    }
    

	public static Driver getDriverByType(String type) {
    	switch (type) {
		case "mysql":
			return MysqlDriver;
		case "postgress":
			return PostgreSQLDriver;
        case "oracle":
            return OracleSQLDriver;
		default:
			return MysqlDriver;
		}

    }
}
