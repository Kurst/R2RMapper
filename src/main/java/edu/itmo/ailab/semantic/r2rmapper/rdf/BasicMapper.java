package edu.itmo.ailab.semantic.r2rmapper.rdf;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import edu.itmo.ailab.semantic.r2rmapper.dbms.DBLoader;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFWriter;

import edu.itmo.ailab.semantic.r2rmapper.dbms.Driver;
import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.properties.PropertyLoader;
import edu.itmo.ailab.semantic.r2rmapper.properties.PropertyType;

/**
 * R2R Mapper. It is a free software.
 *
 * BasicMapper. Class that starts extraction.
 * Author: Ilya Semerhanov
 * Date: 06.08.13
 *
 */
public class BasicMapper {
	
	public static final Logger LOGGER=Logger.getLogger(BasicMapper.class);

	private String tables;
	private String prefix;
	private String jdbcUrl;
	private String dbUser;
	private String dbPassword;
	private List<String> primaryKeys;
	private Driver jdbcDriver; 
	private RDFModelGenerator model = new RDFModelGenerator();
	private List<Object> properties = new ArrayList<>();
	
	public BasicMapper(){

	}

	public BasicMapper(List<Object> properties){
		this.setProperties(properties);  	
	}

    public RDFModelGenerator getModel() {
        return model;
    }

    public String getPrefix() {
		return prefix;
	}

  	public void setPrefix(String prefix) {
		this.prefix = prefix;
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
		if(prefix == null || jdbcDriver == null || dbUser == null || dbPassword == null || jdbcUrl == null){
			throw new R2RMapperException("Not all mandatory parameters were provided");
		}else{
			LOGGER.info("[R2R Mapper] Extracting data from RDB");		
			DBLoader con = new DBLoader(jdbcUrl, dbUser, dbPassword, jdbcDriver);
			con.connect();
			model = new RDFModelGenerator("localhost",prefix);
			model.createModel(0);
			//model.newTableInstance(tableName);
			//con.loadModelFromDB(sqlStatement, model, "id");
			
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
	public void startMetadataExtraction(Map <PropertyType, Object> property)
			throws R2RMapperException, SQLException, InstantiationException,
            IllegalAccessException, ClassNotFoundException{
		
		String name = (String) property.get(PropertyType.NAME);
		String url = (String) property.get(PropertyType.URL);
		String prefix = (String) property.get(PropertyType.PREFIX);
		String jdbcUrl = (String) property.get(PropertyType.JDBCURL);
		String dbUser = (String) property.get(PropertyType.USERNAME);
		String dbPassword = (String) property.get(PropertyType.PASSWORD);
		Driver jdbcDriver = Driver.getDriverByType((String) property.get(PropertyType.TYPE));
        List<String> primaryKeys = (List<String>) property.get(PropertyType.PRIMARYKEYS);
        List<String> tables = (List<String>) property.get(PropertyType.TABLES);

		if(tables == null || prefix == null || jdbcDriver == null ||
                dbUser == null || dbPassword == null || jdbcUrl == null || primaryKeys == null){
			throw new R2RMapperException("Not all mandatory parameters were provided");
		}else{
			LOGGER.info("[R2R Mapper] Extracting data from RDB for " + name);		
			DBLoader con = new DBLoader(jdbcUrl, dbUser, dbPassword, jdbcDriver);
			con.connect();
            model.generateCustomPrefix(prefix,url);
			con.loadDataFromDB(prefix, tables, model, primaryKeys);
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
            throws R2RMapperException, SQLException, InstantiationException,
            IllegalAccessException, ClassNotFoundException{

        String name = (String) property.get(PropertyType.NAME);
        String url = (String) property.get(PropertyType.URL);
        String prefix = (String) property.get(PropertyType.PREFIX);
        String jdbcUrl = (String) property.get(PropertyType.JDBCURL);
        String dbUser = (String) property.get(PropertyType.USERNAME);
        String dbPassword = (String) property.get(PropertyType.PASSWORD);
        Driver jdbcDriver = Driver.getDriverByType((String) property.get(PropertyType.TYPE));
        List<String> tables = (List<String>) property.get(PropertyType.TABLES);

        LOGGER.info("[R2R Mapper] Extracting tables structure from RDB");
        DBLoader con = new DBLoader(jdbcUrl, dbUser, dbPassword, jdbcDriver);
        con.connect();
        model.generateCustomPrefix(prefix,url);
        con.loadStructureFromDB(prefix, tables, model);

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
     * @param filename
	 * @throws IOException 
	 */
	public void printModelToFile(String format, String filename)
            throws IOException{
		
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
	 * First method for preparation of extraction
	 *
     * @param reasoningLevel
     * @param pathToOntology
     * @param ontologyFormat
	 * @throws R2RMapperException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public void extractMetadata(Integer reasoningLevel, String pathToOntology, String ontologyFormat)
			throws R2RMapperException, InstantiationException, 
			IllegalAccessException, ClassNotFoundException, SQLException{
		
		OntModel ont = model.createModel(reasoningLevel);
		model.loadOwlModel(ont, pathToOntology, ontologyFormat);

		for (Object data : this.properties) {
			startMetadataExtraction(PropertyLoader.parseProperty(data));
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
