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

import com.beust.jcommander.JCommander;

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

		CommandLine cls = new CommandLine();
		new JCommander(cls, args);
        PropertyLoader loader;
        BasicMapper bm;

        LOGGER.info("[R2R Mapper] Starting the application");
		try {
            loader = new PropertyLoader(cls.config);
            bm = new BasicMapper(loader.properties);
            try{
                switch (cls.step){
                    case "1":
                        bm.createStructureMap();
                        break;
                    default:
                        throw new R2RMapperException("Step is not defined");
                }
            }catch(Exception ex){
                throw new R2RMapperException("Incorrect step",ex);
            }
        }catch(Exception ex){
            throw new R2RMapperException("Initialization failed",ex);
        }
		//bm2.printModelToFile("RDF/XML","output.rdf");
		bm.printModel("RDF/XML");

	}

}
