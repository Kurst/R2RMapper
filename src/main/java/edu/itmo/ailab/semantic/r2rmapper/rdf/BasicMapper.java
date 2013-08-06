package edu.itmo.ailab.semantic.r2rmapper.rdf;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFWriter;

import edu.itmo.ailab.semantic.r2rmapper.dbms.Driver;
import edu.itmo.ailab.semantic.r2rmapper.dbms.SQLLoader;
import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.properties.PropertyLoader;
import edu.itmo.ailab.semantic.r2rmapper.properties.PropertyType;

public class BasicMapper {
	
	public static final Logger LOGGER=Logger.getLogger(BasicMapper.class);
	
	private String tableName;
	private String prefix;
	private String sqlStatement;
	private String jdbcUrl;
	private String dbUser;
	private String dbPassword;
	private String primaryKey;
	private Driver jdbcDriver; 
	private RDFModelGenerator model = new RDFModelGenerator();
	private List<Object> properties = new ArrayList<>();
	
	public BasicMapper(){


	}

	public BasicMapper(List<Object> properties){
		this.setProperties(properties);  	
	}
	
	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public String getPrefix() {
		return prefix;
	}


	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}


	public String getSqlStatement() {
		return sqlStatement;
	}


	public void setSqlStatement(String sqlStatement) {
		this.sqlStatement = sqlStatement;
	}


	public String getJdbcUrl() {
		return jdbcUrl;
	}


	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}


	public String getDbUser() {
		return dbUser;
	}


	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}


	public String getDbPassword() {
		return dbPassword;
	}


	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}


	public Driver getJdbcDriver() {
		return jdbcDriver;
	}


	public void setJdbcDriver(Driver jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}
	
	public List<Object> getProperties() {
		return properties;
	}

	public void setProperties(List<Object> properties) {
		this.properties = properties;
	}
	
	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * Extract data into new model.
	 * 
	 * @throws R2RMapperException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@Deprecated
	public void startSingleExtraction() 
			throws R2RMapperException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		if(tableName == null || prefix == null || sqlStatement == null 
				|| jdbcDriver == null || dbUser == null || dbPassword == null || jdbcUrl == null){
			throw new R2RMapperException("Not all mandatory parameters were provided");
		}else{
			LOGGER.info("[R2R Mapper] Extracting data from RDB");		
			SQLLoader con = new SQLLoader(jdbcUrl, dbUser, dbPassword, jdbcDriver);
			con.connect();
			model = new RDFModelGenerator("localhost",prefix);
			model.createModel(0);
			model.newTableInstance(tableName);
			con.loadModelFromDB(sqlStatement, model, "id");
			
		}
	}
	
	/**
	 * Method extracts data from DBMS into RDF model, based on properties from yaml file.
	 * 
	 * @param property
	 * @throws R2RMapperException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public void startExtraction(Map <PropertyType, Object> property)
			throws R2RMapperException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		
		String name = (String) property.get(PropertyType.NAME);
		String url = (String) property.get(PropertyType.URL);
		String prefix = (String) property.get(PropertyType.PREFIX);
		String tableName = (String) property.get(PropertyType.TABLENAME);
		String jdbcUrl = (String) property.get(PropertyType.JDBCURL);
		String dbUser = (String) property.get(PropertyType.USERNAME);
		String dbPassword = (String) property.get(PropertyType.PASSWORD);
		Driver jdbcDriver = Driver.getDriverByType((String) property.get(PropertyType.TYPE));
		String sqlStatement = (String) property.get(PropertyType.QUERY);
		String primaryKey = (String) property.get(PropertyType.PRIMARYKEY);
		
		if(tableName == null || prefix == null || sqlStatement == null 
				|| jdbcDriver == null || dbUser == null || dbPassword == null || jdbcUrl == null || primaryKey == null){
			throw new R2RMapperException("Not all mandatory parameters were provided");
		}else{
			LOGGER.info("[R2R Mapper] Extracting data from RDB for " + name);		
			SQLLoader con = new SQLLoader(jdbcUrl, dbUser, dbPassword, jdbcDriver);
			con.connect();
			model.setPrefix(prefix);
			model.setBaseNamespace(url);
			model.setSystemNamespace(model.getBaseNamespace() + prefix + "#");
			model.addNsPrefix(prefix);
			//model.newTableInstance(tableName);
			//OntModel ontModel = model.getOntModel();
			//ontModel.createClass("sak:RdbData");
			con.loadModelFromDB(sqlStatement, model, primaryKey);
			
		}
		
	}

    /**
     * Method extracts structure of database tables.
     *
     * @param property
     * @throws R2RMapperException
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void startStructureExtraction(Map <PropertyType, Object> property)
            throws R2RMapperException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{

        String name = (String) property.get(PropertyType.NAME);
        String url = (String) property.get(PropertyType.URL);
        String prefix = (String) property.get(PropertyType.PREFIX);
        String jdbcUrl = (String) property.get(PropertyType.JDBCURL);
        String dbUser = (String) property.get(PropertyType.USERNAME);
        String dbPassword = (String) property.get(PropertyType.PASSWORD);
        Driver jdbcDriver = Driver.getDriverByType((String) property.get(PropertyType.TYPE));
        List<String> tables = (List<String>) property.get(PropertyType.TABLES);

        LOGGER.info("[R2R Mapper] Extracting tables structure from RDB");
        SQLLoader con = new SQLLoader(jdbcUrl, dbUser, dbPassword, jdbcDriver);
        con.connect();
        model.generateCustomPrefix(prefix,url);
        con.loadStructureFromDB(tables, model);

    }
	
	/**
	 * Prints RDF model in console
	 * @param format
	 * @throws FileNotFoundException 
	 */
	public void printModel(String format){
		
		LOGGER.info("[R2R Mapper] Printing output into console");
		
		OntModel ontModel = model.getOntModel();
		RDFWriter rdfWriter = ontModel.getWriter(format);
		rdfWriter.setProperty("width", String.valueOf(Integer.MAX_VALUE));
		rdfWriter.write(ontModel, System.out, null);
		System.out.println("\n");
	}
	
	/**
	 * Prints RDF model in file
	 * @param format
	 * @throws IOException 
	 */
	public void printModelToFile(String format, String filename) throws IOException{
		
		LOGGER.info("[R2R Mapper] Printing output into file: output/" + filename);
		
		new File("output").mkdirs();
		File file = new File("output/" + filename);
		
		OntModel ontModel = model.getOntModel();
		RDFWriter rdfWriter = ontModel.getWriter(format);
		rdfWriter.setProperty("width", String.valueOf(Integer.MAX_VALUE));
		rdfWriter.write(ontModel, new FileOutputStream(file), null);
		System.out.println("\n");
		//transformToUTF8(new File("output/" + filename), "UTF-8", new File("output/" + "ut8_" + filename), "ascii");		
	}
	
	/**
	 * Creates new RDF model object
	 * 
	 * @throws R2RMapperException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void createMap(Integer reasoningLevel) 
			throws R2RMapperException, InstantiationException, 
			IllegalAccessException, ClassNotFoundException, SQLException{
		
		OntModel ont = model.createModel(reasoningLevel);
		model.loadOwlModel(ont);
		for (Object data : this.properties) {
			startExtraction(PropertyLoader.parseProperty(data));		
	    }
	}

    /**
     * Creates new RDF model object of DB structure
     *
     * @throws R2RMapperException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void createStructureMap()
            throws R2RMapperException, InstantiationException,
            IllegalAccessException, ClassNotFoundException, SQLException{

        OntModel ont = model.createModel(0);
        for (Object data : this.properties) {
            startStructureExtraction(PropertyLoader.parseProperty(data));
        }
    }

	
	
}
