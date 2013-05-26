package edu.itmo.ailab.semantic.r2rmapper.main;

/**
 * 
 * R2RMapper Main
 *
 * Interface here.
 * 
 *
 */

import java.sql.SQLException;

import org.apache.log4j.Logger;



import edu.itmo.ailab.semantic.r2rmapper.dbms.Driver;
import edu.itmo.ailab.semantic.r2rmapper.dbms.SQLLoader;
import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
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
	 */
	public static void main(String[] args)
			throws SQLException, IllegalAccessException, 
			ClassNotFoundException, InstantiationException, R2RMapperException {
		
		String table = "test";
		String table2 = "news";
		SQLLoader c = new SQLLoader("jdbc:mysql://localhost/r2rmapper", "root", "", Driver.MysqlDriver);
		c.connect();
		RDFModelGenerator m = new RDFModelGenerator("localhost","r2r");
		m.createModel();
		//m.newClassMapInstance(table);
		m.addTripple("123", "456");
		//c.loadModelFromDB("SELECT * from " + table + ";", m);
		//m.newClassMapInstance(table2);
		//c.connect();
		//c.loadModelFromDB("SELECT * from " + table2 + ";", m);
		
		
		m.showModel("TURTLE");
		
		
		
		
		
	

	}

}
