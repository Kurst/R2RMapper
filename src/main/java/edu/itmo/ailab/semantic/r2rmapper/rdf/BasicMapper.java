package edu.itmo.ailab.semantic.r2rmapper.rdf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFWriter;

import edu.itmo.ailab.semantic.r2rmapper.dbms.Driver;
import edu.itmo.ailab.semantic.r2rmapper.dbms.SQLLoader;
import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.properties.PropertyType;

public class BasicMapper {
	
	public static final Logger LOGGER=Logger.getLogger(BasicMapper.class);
	
	private String tableName;
	private String prefix;
	private String sqlStatement;
	private String jdbcUrl;
	private String dbUser;
	private String dbPassword;
	private Driver jdbcDriver; 
	private RDFModelGenerator model = new RDFModelGenerator();
	private List<Object> properties = new ArrayList<Object>();
	
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
			model.createModel();
			model.newTableInstance(tableName);
			con.loadModelFromDB(sqlStatement, model);
			
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
	public void startExtraction(Map <PropertyType, String> property) 
			throws R2RMapperException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		
		String name = property.get(PropertyType.NAME);
		String url = property.get(PropertyType.URL);
		String prefix = property.get(PropertyType.PREFIX);
		String tableName = property.get(PropertyType.TABLENAME);
		String jdbcUrl = property.get(PropertyType.JDBCURL);
		String dbUser = property.get(PropertyType.USERNAME);
		String dbPassword = property.get(PropertyType.PASSWORD);
		Driver jdbcDriver = Driver.getDriverByType(property.get(PropertyType.TYPE));
		String sqlStatement = property.get(PropertyType.QUERY);
		
		if(tableName == null || prefix == null || sqlStatement == null 
				|| jdbcDriver == null || dbUser == null || dbPassword == null || jdbcUrl == null){
			throw new R2RMapperException("Not all mandatory parameters were provided");
		}else{
			LOGGER.info("[R2R Mapper] Extracting data from RDB for " + name);		
			SQLLoader con = new SQLLoader(jdbcUrl, dbUser, dbPassword, jdbcDriver);
			con.connect();
			model.setPrefix(prefix);
			model.setBaseNamespace(url);
			model.setSystemNamespace(model.getBaseNamespace() + prefix + "#");
			model.addNsPrefix(prefix);
			model.newTableInstance(tableName);
			con.loadModelFromDB(sqlStatement, model);
			
		}
		
	}
	
	/**
	 * Prints RDF model in console
	 * @param format
	 */
	public void printModel(String format){
		OntModel ontModel = model.getOntModel();
		RDFWriter rdfWriter = ontModel.getWriter(format);
		rdfWriter.setProperty("width", String.valueOf(Integer.MAX_VALUE));
		rdfWriter.write(ontModel, System.out, null);
		System.out.println("\n");
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
	public void createMap() 
			throws R2RMapperException, InstantiationException, 
			IllegalAccessException, ClassNotFoundException, SQLException{
		
		model.createModel();
		for (Object data : this.properties) {
			startExtraction(this.parseProperty(data));		
	    }
	}
	
	/**
	 * Parse properties from Object.
	 * 
	 * @param prop
	 * @return
	 * @throws R2RMapperException
	 */
	@SuppressWarnings("rawtypes")
	public Map <PropertyType, String> parseProperty(Object prop) 
			throws R2RMapperException{
		Map yamlProperty = (Map) prop;
		Map yamlDBProperty = (Map) yamlProperty.get("Database");
		Map <PropertyType, String> property;
		property = new HashMap<PropertyType, String>();
		
		if((String) yamlProperty.get("Name") != null){
			property.put(PropertyType.NAME, (String) yamlProperty.get("Name"));
		}else{
			throw new R2RMapperException("Property Name is missing");
		}
		
		if((String) yamlProperty.get("Url") != null){
			property.put(PropertyType.URL, (String) yamlProperty.get("Url"));
		}else{
			throw new R2RMapperException("Property Url is missing");
		}
		
		if((String) yamlProperty.get("Prefix") != null){
			property.put(PropertyType.PREFIX, (String) yamlProperty.get("Prefix"));
		}else{
			throw new R2RMapperException("Property Prefix is missing");
		}
		
		if((String) yamlDBProperty.get("JdbcUrl") != null ){
			property.put(PropertyType.JDBCURL, (String) yamlDBProperty.get("JdbcUrl"));
		}else{
			throw new R2RMapperException("Property JdbcUrl is missing");
		}
		
		if((String) yamlDBProperty.get("Username") != null ){
			property.put(PropertyType.USERNAME, (String) yamlDBProperty.get("Username"));
		}else{
			throw new R2RMapperException("Property Username is missing");
		}
		
		if((String) yamlDBProperty.get("Password") != null ){
			property.put(PropertyType.PASSWORD, (String) yamlDBProperty.get("Password"));
		}else{
			property.put(PropertyType.PASSWORD, ""); // provide empty password
		}
		
		if((String) yamlDBProperty.get("Type") != null ){
			property.put(PropertyType.TYPE, (String) yamlDBProperty.get("Type"));
		}else{
			throw new R2RMapperException("Property Type is missing");
		}
		
		if((String) yamlDBProperty.get("Query") != null ){
			property.put(PropertyType.QUERY, (String) yamlDBProperty.get("Query"));
		}else{
			throw new R2RMapperException("Property Query is missing");
		}
		
		if((String) yamlDBProperty.get("TableName") != null ){
			property.put(PropertyType.TABLENAME, (String) yamlDBProperty.get("TableName"));
		}else{
			throw new R2RMapperException("Property TableName is missing");
		}
		
		return property;	
		
	}

	
	
}
