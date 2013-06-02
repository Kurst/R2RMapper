package edu.itmo.ailab.semantic.r2rmapper.dbms;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.hp.hpl.jena.rdf.model.Resource;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSetMetaData;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.rdf.RDFModelGenerator;

import org.apache.log4j.Logger;




public class SQLLoader {

	public static final Logger LOGGER=Logger.getLogger(SQLLoader.class);
	
	private String jdbcURL;
	private String username;
	private String password; 
	private Driver driver;
	protected static Connection conn = null;
	
	public SQLLoader(String jdbcURL, String username, String password, Driver driver) 
			throws SQLException, InstantiationException, IllegalAccessException, R2RMapperException {
		
		if (jdbcURL != null && !jdbcURL.toLowerCase().startsWith("jdbc:")) {
			throw new R2RMapperException("Not a JDBC URL: " + jdbcURL);
		}
		this.setJdbcURL(jdbcURL);
		this.setUsername(username);
		this.setPassword(password);
		this.setDriver(driver);
		SQLLoader.registerJDBCDriver(driver);
	}
	
	public String getJdbcURL() {
		return jdbcURL;
	}

	public void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}
	
	public static void registerJDBCDriver(Driver driver) 
			throws InstantiationException, IllegalAccessException, R2RMapperException {
		try {
			Class.forName(driver.getDriverName()).newInstance();
		} catch (ClassNotFoundException ex) {
			throw new R2RMapperException("Driver not found", ex);
		}
	}
	
	public void connect() 
			throws SQLException,IllegalAccessException,
			ClassNotFoundException, InstantiationException {
		
		try {
			LOGGER.info("[SQL Loader] Establishing connection to database with: " + jdbcURL);
			conn = (Connection) DriverManager.getConnection(jdbcURL, username, password);
			LOGGER.info("[SQL Loader] Database connection established.");
		} catch (SQLException ex) {
			throw new SQLException(
					"[SQL Loader] Database connection to " + jdbcURL + " failed " +
					"(user: " + username + "): " + ex.getMessage());
		}
		
				
		
	}
	
	public void loadModelFromDB(String query, RDFModelGenerator model)
			throws SQLException, R2RMapperException {
		Statement statement;
		ResultSet resultSet;
		ResultSetMetaData resultSetMetaData;
		int numColumns;
		statement = null;
		resultSet = null;
		
		LOGGER.debug("[SQL Loader: loadModelFromDB] executing query: " + query);
		try {
			
			statement = conn.createStatement();
			resultSet = statement.executeQuery(query);			
			resultSetMetaData = (ResultSetMetaData) resultSet.getMetaData();
			numColumns = resultSetMetaData.getColumnCount();
			while (resultSet.next()) {
				Resource r = model.newInstance( resultSet.getString("id"), resultSetMetaData.getTableName(1));
				for (int col = 1; col <= numColumns; ++col) {
					model.addStatement(r, resultSetMetaData.getColumnName(col), resultSet.getString(col));	
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (Throwable t) {
					LOGGER.error("Failed to close result set", t);
				}
			}
			if (statement != null) {
				try {
					statement.close();
				} catch (Throwable t) {
					LOGGER.error("Failed to close statement", t);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Throwable t) {
					LOGGER.error("Failed to close connection", t);
				}
			}
		}
	}

	
}


