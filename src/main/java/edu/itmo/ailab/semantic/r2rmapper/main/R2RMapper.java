package edu.itmo.ailab.semantic.r2rmapper.main;

import edu.itmo.ailab.semantic.r2rmapper.dbms.RedisHandler;
import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.properties.PropertyLoader;
import edu.itmo.ailab.semantic.r2rmapper.rdf.BasicMapper;


/**
 * R2R Mapper. It is a free software.
 *
 * Main class.
 * Author: Ilya Semerhanov
 * Date: 06.08.13
 */
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
        String outputFileNamePhase1 = "integrated_ontology_phase_1.owl";
        String outputFileNamePhase2 = "integrated_ontology_phase_2.owl";
        String ontologyFormat = "RDF/XML";

        LOGGER.info("[R2R Mapper] Starting the application");
		try {
            if(cls.settings == null){
                RedisHandler.getInstance("src/main/resources/settings.yaml");
            }else{
                RedisHandler.getInstance(cls.settings);
            }
            RedisHandler.connect();
            loader = new PropertyLoader(cls.config);
            bm = new BasicMapper(loader.properties);
            try{
                switch (cls.phase){
                    case "1":
                        RedisHandler.flushDB();
                        bm.createStructureMap();
                        bm.printModelToFile(ontologyFormat,outputFileNamePhase1);
                        //bm.printModel(ontologyFormat);
                        break;
                    case "2":
                        if(cls.pathToOntology != null){
                            bm.extractMetadata(1, cls.pathToOntology, ontologyFormat);    //reasoning with Pellet
                            bm.printModelToFile(ontologyFormat,outputFileNamePhase2);
                            //bm.printModel("TURTLE");
                        }else{
                            throw new R2RMapperException("For phase 2 ontology file is not defined");
                        }

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

        RedisHandler.saveDatasetToDisk();
	}

}
