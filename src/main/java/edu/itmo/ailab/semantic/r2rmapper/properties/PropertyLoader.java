package edu.itmo.ailab.semantic.r2rmapper.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;


public class PropertyLoader {
	
	public static final Logger LOGGER=Logger.getLogger(PropertyLoader.class);
	
	public String filePath;
	
	public List<Object> properties = new ArrayList<Object>();
	
	public PropertyLoader(String filePath)
			throws FileNotFoundException{
		this.filePath = filePath;
		
		InputStream input = new FileInputStream(new File(filePath));
		Yaml yaml = new Yaml();
		for (Object data : yaml.loadAll(input)) {
			LOGGER.info("[ProperyLoader] Loading property: " +data);
			properties.add(data);
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
	public static Map <PropertyType, String> parseProperty(Object prop) 
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
		
		if((String) yamlDBProperty.get("PrimaryKey") != null ){
			property.put(PropertyType.PRIMARYKEY, (String) yamlDBProperty.get("PrimaryKey"));
		}else{
			throw new R2RMapperException("Property PrimaryKey is missing");
		}
		
		return property;	
		
	}
	
	/*public void parse() 
			throws FileNotFoundException{
		InputStream input = new FileInputStream(new File(filePath));
		Yaml yaml = new Yaml();
	    for (Object data : yaml.loadAll(input)) {
	        //System.out.println(data);
	    	Map o = (Map) data;
	    	System.out.println(o.get("Stack"));
	    	
	        
	 
		
	    
	}
	    }	*/

}
