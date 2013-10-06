package edu.itmo.ailab.semantic.r2rmapper.rdf;

import com.hp.hpl.jena.ontology.OntModel;
import edu.itmo.ailab.semantic.r2rmapper.dbms.DBLoader;
import edu.itmo.ailab.semantic.r2rmapper.dbms.Driver;
import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.properties.PropertyLoader;
import edu.itmo.ailab.semantic.r2rmapper.properties.PropertyType;
import edu.itmo.ailab.semantic.r2rmapper.rdf.impl.ConsoleMapperImpl;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BasicMapper {
    public static final Logger LOGGER = Logger.getLogger(ConsoleMapperImpl.class);
    private String tables;
    private String prefix;
    private String jdbcUrl;
    private String dbUser;
    private String dbPassword;
    private List<String> primaryKeys;
    private Driver jdbcDriver;
    protected RDFModelGenerator model = new RDFModelGenerator();
    private List<Object> properties = new ArrayList<Object>();

    public BasicMapper() {
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
     * Method extracts data from DBMS into RDF model, based on properties from yaml file.
     *
     * @param property
     * @throws edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException
     *
     * @throws java.sql.SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void startMetadataExtraction(Map<PropertyType, Object> property)
            throws R2RMapperException, SQLException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {

        String name = (String) property.get(PropertyType.NAME);
        String url = (String) property.get(PropertyType.URL);
        String prefix = (String) property.get(PropertyType.PREFIX);
        String jdbcUrl = (String) property.get(PropertyType.JDBCURL);
        String dbUser = (String) property.get(PropertyType.USERNAME);
        String dbPassword = (String) property.get(PropertyType.PASSWORD);
        Driver jdbcDriver = Driver.getDriverByType((String) property.get(PropertyType.TYPE));
        List<String> primaryKeys = (List<String>) property.get(PropertyType.PRIMARYKEYS);
        List<String> tables = (List<String>) property.get(PropertyType.TABLES);

        if (tables == null || prefix == null || jdbcDriver == null ||
                dbUser == null || dbPassword == null || jdbcUrl == null || primaryKeys == null) {
            throw new R2RMapperException("Not all mandatory parameters were provided");
        } else {
            LOGGER.info("[R2R Mapper] Extracting data from RDB for " + name);
            DBLoader con = new DBLoader(jdbcUrl, dbUser, dbPassword, jdbcDriver);
            con.connect();
            model.generateCustomPrefix(prefix, url);
            con.loadDataFromDB(prefix, tables, model, primaryKeys);
        }

    }

    /**
     * Method extracts structure of database tables.
     *
     * @param property
     * @throws edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException
     *
     * @throws java.sql.SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void startStructureExtraction(Map<PropertyType, Object> property)
            throws R2RMapperException, SQLException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {

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
        model.generateCustomPrefix(prefix, url);
        con.loadStructureFromDB(prefix, tables, model);

    }

    /**
     * Prints RDF model in console
     *
     * @param format
     * @throws java.io.FileNotFoundException
     */
    public abstract void printModel(String format);

    /**
     * Prints RDF model in file
     *
     * @param format
     * @param filename
     * @throws java.io.IOException
     */
    public abstract void printModelToFile(String format, String filename, String path)
            throws IOException;

    /**
     * First method for preparation of extraction
     *
     * @param reasoningLevel
     * @param pathToOntology
     * @param ontologyFormat
     * @throws edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public void extractMetadata(Integer reasoningLevel, String pathToOntology, String ontologyFormat)
            throws R2RMapperException, InstantiationException,
            IllegalAccessException, ClassNotFoundException, SQLException {

        OntModel ont = model.createModel(reasoningLevel);
        model.loadOwlModel(ont, pathToOntology, ontologyFormat);

        for (Object data : this.properties) {
            startMetadataExtraction(PropertyLoader.parseProperty(data));
        }
    }

    /**
     * Creates new RDF model object of DB structure
     *
     * @throws edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public void createStructureMap()
            throws R2RMapperException, InstantiationException,
            IllegalAccessException, ClassNotFoundException, SQLException {

        OntModel ont = model.createModel(0);
        for (Object data : this.properties) {
            startStructureExtraction(PropertyLoader.parseProperty(data));
        }
    }
}