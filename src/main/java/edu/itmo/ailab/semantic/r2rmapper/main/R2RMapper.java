package edu.itmo.ailab.semantic.r2rmapper.main;

/**
 * 
 * R2RMapper Main
 *
 * Interface here.
 * 
 *
 */

import org.apache.log4j.Logger;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.properties.PropertyLoader;
import edu.itmo.ailab.semantic.r2rmapper.rdf.BasicMapper;



public class R2RMapper {
	
	public static final Logger LOGGER=Logger.getLogger(R2RMapper.class);
	/**
	 * Main run method.
	 * 
	 * @param args
	 * @throws R2RMapperException 
	 */
	public static void main(String[] args)
			throws Exception {
				
		/*BasicMapper bm = new BasicMapper();
		bm.setJdbcUrl("jdbc:mysql://localhost/r2r");
		bm.setDbUser("root");
		bm.setDbPassword("");
		bm.setJdbcDriver(Driver.MysqlDriver);
		bm.setPrefix("cde");
		bm.setTableName("test");
		bm.setSqlStatement("SELECT * from " + bm.getTableName() + ";");
		bm.startExtraction();
		bm.printModel("TURTLE");*/
		
		PropertyLoader loader = new PropertyLoader("src/main/resources/config.yaml");
		BasicMapper bm2 = new BasicMapper(loader.properties);
		bm2.createMap();
		bm2.printModel("TURTLE");
		

	}

}
