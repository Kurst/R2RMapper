package edu.itmo.ailab.semantic.r2rmapper.rdf;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFWriter;

import edu.itmo.ailab.semantic.r2rmapper.dbms.Driver;
import edu.itmo.ailab.semantic.r2rmapper.dbms.SQLLoader;
import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;

public class BasicMapper {
	
	public static final Logger LOGGER=Logger.getLogger(BasicMapper.class);
	
	private String tableName;
	private String prefix;
	private String sqlStatement;
	private String jdbcUrl;
	private String dbUser;
	private String dbPassword;
	private Driver jdbcDriver; 
	private RDFModelGenerator model;
	
	public BasicMapper(){
		
		
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

	public void startExtraction() 
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
	
	public void printModel(String format){
		OntModel ontModel = model.getOntModel();
		RDFWriter rdfWriter = ontModel.getWriter(format);
		rdfWriter.setProperty("width", String.valueOf(Integer.MAX_VALUE));
		rdfWriter.write(ontModel, System.out, null);
		System.out.println("\n");
	}
	
	

	
	
}
