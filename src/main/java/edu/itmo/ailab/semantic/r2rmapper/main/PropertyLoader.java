package edu.itmo.ailab.semantic.r2rmapper.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;


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
