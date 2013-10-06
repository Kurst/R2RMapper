package edu.itmo.ailab.semantic.r2rmapper.main;

import edu.itmo.ailab.semantic.r2rmapper.comparator.IndividualsComparator;
import edu.itmo.ailab.semantic.r2rmapper.dbms.MatchingDBHandler;
import edu.itmo.ailab.semantic.r2rmapper.rdf.TDBManager;
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
        IndividualsComparator ic;
        String outputFileNamePhase1 = "integrated_ontology_phase_1.owl";
        String outputFileNamePhase2 = "integrated_ontology_phase_2.owl";
        String ontologyFormat = "TURTLE";

        LOGGER.info("[R2R Mapper] Starting the application");
		try {
            if(cls.settings == null){
                MatchingDBHandler.getInstance("src/main/resources/settings.yaml");
            }else{
                MatchingDBHandler.getInstance(cls.settings);
            }
            MatchingDBHandler.connect();
            loader = new PropertyLoader(cls.config);
            bm = new BasicMapper(loader.properties);
            ic = new IndividualsComparator();
            try{
                switch (cls.phase){
                    case "1":   //Phase for extracting structure
                        MatchingDBHandler.flushDB();
                        bm.createStructureMap();
                        bm.printModelToFile(ontologyFormat,outputFileNamePhase1,"");
                        //bm.printModel(ontologyFormat);
                        break;
                    case "2":   //Phase for extracting and comparing individuals
                        if(cls.pathToOntology != null){
                            bm.extractMetadata(1, cls.pathToOntology, ontologyFormat);    //reasoning with Pellet

                            //bm.printModel("TURTLE");
                            if(cls.compare){
                                ic.analyzeSimilarity(bm.getModel().getOntModel());
                            }
                            bm.printModelToFile(ontologyFormat,outputFileNamePhase2,"");

                        }else{
                            throw new R2RMapperException("For phase 2 ontology file is not defined");
                        }

                        break;
                    case "3":   //Phase for storing data in TDB
                        TDBManager tdb = new TDBManager("output/tdb");
                        String pathToOntology;
                        if(cls.pathToOntology == null){
                            pathToOntology = "output/"+outputFileNamePhase2;
                        }else{
                            pathToOntology = cls.pathToOntology;
                        }
                        tdb.importFromFile(pathToOntology,ontologyFormat);
                        tdb.showAllQuery();
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

        MatchingDBHandler.saveDatasetToDisk();
	}

}
