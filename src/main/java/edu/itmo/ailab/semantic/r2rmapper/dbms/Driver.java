package edu.itmo.ailab.semantic.r2rmapper.dbms;



public class Driver {
	public static Driver MysqlDriver = new Driver("com.mysql.jdbc.Driver");
    public static Driver PostgreSQLDriver = new Driver("org.postgresql.Driver");

    private String driverName;
    
    public Driver(String driverName) {
    	this.driverName=  driverName;
    }
    
    public String getDriverName() {
    	return driverName;
    }
}
