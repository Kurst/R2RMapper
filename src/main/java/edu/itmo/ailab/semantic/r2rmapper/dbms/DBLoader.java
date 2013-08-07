package edu.itmo.ailab.semantic.r2rmapper.dbms;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import com.hp.hpl.jena.rdf.model.Resource;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSetMetaData;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.rdf.RDFModelGenerator;

import org.apache.log4j.Logger;

/**
 * R2R Mapper. It is a free software.
 *
 * Class for extracting data from RDBMS.
 * Author: Ilya Semerhanov
 * Date: 06.08.13
 */
public class DBLoader {

    public static final Logger LOGGER = Logger.getLogger(DBLoader.class);

    private String jdbcURL;
    private String username;
    private String password;
    private Driver driver;
    protected static Connection conn = null;

    public DBLoader(String jdbcURL, String username, String password, Driver driver)
            throws SQLException, InstantiationException, IllegalAccessException, R2RMapperException {

        if (jdbcURL != null && !jdbcURL.toLowerCase().startsWith("jdbc:")) {
            throw new R2RMapperException("Not a JDBC URL: " + jdbcURL);
        }
        this.setJdbcURL(jdbcURL);
        this.setUsername(username);
        this.setPassword(password);
        this.setDriver(driver);
        DBLoader.registerJDBCDriver(driver);
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

    public void setUsername(String username) {
        this.username = username;
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
            throws SQLException, IllegalAccessException,
            ClassNotFoundException, InstantiationException {

        try {
            LOGGER.info("[DB Loader] Establishing connection to database with: " + jdbcURL);
            Properties connInfo = new Properties();
            connInfo.put("user", username);
            connInfo.put("password", password);
            connInfo.put("useUnicode", "true");
            conn = (Connection) DriverManager.getConnection(jdbcURL, connInfo);
            LOGGER.info("[DB Loader] Database connection established.");
        } catch (SQLException ex) {
            throw new SQLException(
                    "[DB Loader] Database connection to " + jdbcURL + " failed " +
                            "(user: " + username + "): " + ex.getMessage());
        }


    }

    public void loadModelFromDB(String query, RDFModelGenerator model, String primaryKey)
            throws SQLException, R2RMapperException {
        Statement statement;
        ResultSet resultSet;
        ResultSetMetaData resultSetMetaData;
        int numColumns;
        statement = null;
        resultSet = null;

        LOGGER.debug("[DB Loader] executing query: " + query);
        try {

            statement = conn.createStatement();
            resultSet = statement.executeQuery(query);
            resultSetMetaData = (ResultSetMetaData) resultSet.getMetaData();
            numColumns = resultSetMetaData.getColumnCount();
            while (resultSet.next()) {
                Resource r = model.newInstance(resultSet.getString(primaryKey), resultSetMetaData.getTableName(1));
                for (int col = 1; col <= numColumns; ++col) {
                    model.addStatement(r, resultSetMetaData.getColumnName(col), resultSet.getString(col));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
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

    public void loadStructureFromDB(String systemName, List<String> tables, RDFModelGenerator model)
            throws SQLException, R2RMapperException {
        Statement statement = null;
        ResultSet resultSet = null;
        ResultSetMetaData metadata;
        Resource classMap;
        Resource dataTypeProperty;
        String query;

        for (String tableName : tables) {
            LOGGER.debug("[DB Loader] preparing query for " + tableName);
            query = "SELECT * FROM " + tableName + " WHERE 0 = 1;";
            try {
                statement = conn.createStatement();
                resultSet = statement.executeQuery(query);
                metadata = (ResultSetMetaData) resultSet.getMetaData();
                classMap = model.newTableInstance(tableName);
                String redisKey = (systemName + "_" + tableName).replaceAll(" ", "_");
                RedisHandler.addClassMap(redisKey,tableName,classMap.getURI());
                for (int col = 1; col <= metadata.getColumnCount(); ++col) {
                    dataTypeProperty = model.addDatatypeProperty(metadata.getColumnName(col),
                            metadata.getColumnTypeName(col), classMap, tableName);
                    RedisHandler.addDataTypeProperty(redisKey,metadata.getColumnName(col),dataTypeProperty.getURI());
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

        }

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

