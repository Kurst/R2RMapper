package edu.itmo.ailab.semantic.r2rmapper.main;

/**
 * 
 * R2RMapper Main
 *
 * Interface here.
 * 
 *
 */

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.apache.log4j.Logger;



import edu.itmo.ailab.semantic.r2rmapper.dbms.Driver;
import edu.itmo.ailab.semantic.r2rmapper.dbms.SQLLoader;
import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.rdf.BasicMapper;
import edu.itmo.ailab.semantic.r2rmapper.rdf.RDFModelGenerator;


public class R2RMapper {
	
	public static final Logger LOGGER=Logger.getLogger(R2RMapper.class);
	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws SQLException 
	 * @throws InstantiationException 
	 * @throws R2RMapperException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args)
			throws SQLException, IllegalAccessException, 
			ClassNotFoundException, InstantiationException, R2RMapperException, FileNotFoundException {
				
		/*BasicMapper bm = new BasicMapper();
		bm.setJdbcUrl("jdbc:mysql://localhost/r2rmapper");
		bm.setDbUser("root");
		bm.setDbPassword("");
		bm.setJdbcDriver(Driver.MysqlDriver);
		bm.setPrefix("cde");
		bm.setTableName("test");
		bm.setSqlStatement("SELECT * from " + bm.getTableName() + ";");
		bm.startExtraction();
		bm.printModel("TURTLE");*/
		
		PropertyLoader loader = new PropertyLoader("src/main/resources/test.yaml");
		BasicMapper bm2 = new BasicMapper(loader.properties);
		

	}

}
