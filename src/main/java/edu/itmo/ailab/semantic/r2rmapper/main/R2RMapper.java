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
import org.apache.log4j.lf5.util.Resource;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.VCARD;

import edu.itmo.ailab.semantic.r2rmapper.dbms.Driver;
import edu.itmo.ailab.semantic.r2rmapper.dbms.SQLLoader;
import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.rdf.RDFModelGeneratorImpl;


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
		
		//System.out.println("Test1");
		//LOGGER.info("Hello World!");
		
		/*SQLLoader c = new SQLLoader("jdbc:mysql://localhost/r2rmapper", "root", "", Driver.MysqlDriver);
		c.connect();
		c.loadModelFromDB("SELECT * from test;");*/
		
		RDFModelGeneratorImpl m = new RDFModelGeneratorImpl("localhost");
		m.createModel();
		m.newInstance("test");
		m.addStatement("test", "id", "123");
		m.showModel("Turtle");
		
		
	

	}

}
