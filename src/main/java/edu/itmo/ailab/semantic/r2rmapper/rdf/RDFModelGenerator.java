package edu.itmo.ailab.semantic.r2rmapper.rdf;


import java.io.FileInputStream;

import java.lang.*;

import com.hp.hpl.jena.rdf.model.*;
import org.apache.log4j.Logger;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.OWL;



import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;

/**
 * R2R Mapper. It is a free software.
 *
 * RDFModelGenerator. Class that generates RDF model from DB.
 * Author: Ilya Semerhanov
 * Date: 06.08.13
 *
 */
public class RDFModelGenerator{
	
	public static final Logger LOGGER=Logger.getLogger(RDFModelGenerator.class);
	
	private OntModel ontModel;
	private String modelName = "";
	public String baseNamespace = "http://localhost/";
	public String systemNamespace = "";
	public String prefix = "";

	public RDFModelGenerator(){

	}

	public RDFModelGenerator(String modelName, String prefix){
		
		this.modelName = modelName;
		this.prefix = prefix;
		this.baseNamespace = baseNamespace + prefix + "#";
	}
	
	public OntModel getOntModel() {
		return ontModel;
	}

	public void setOntModel(OntModel ontModel) {
		this.ontModel = ontModel;
	}
	
	public String getBaseNamespace() {
		return baseNamespace;
	}

	public void setBaseNamespace(String baseNamespace) {
		this.baseNamespace = baseNamespace;
	}
	
	public void setSystemNamespace(String ns) {
		this.systemNamespace = ns;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * Create ontology model 
	 * 
	 * @return
	 * @throws R2RMapperException
	 */
	public OntModel createModel(Integer reasoningLevel) 
			throws R2RMapperException {	
		try{
			LOGGER.info("[RDF Model Generator] Creating new RDF model: " + modelName);
			
			if(reasoningLevel == 0){
				ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);	
			}else if (reasoningLevel == 1) { // OWL
				Reasoner reasoner = PelletReasonerFactory.theInstance().create();
				Model infModel = ModelFactory.createInfModel(reasoner, ModelFactory.createDefaultModel());
				ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, infModel);
			}
			return ontModel;
		}catch(Exception ex){
			throw new R2RMapperException("[RDF Model Generator] New RDF model creation failed", ex);
		}
	}
	
	/**
	 * Add namespace prefix into RDF model
	 * @param nsPrefix
	 */
	public void addNsPrefix(String nsPrefix) {
		ontModel.setNsPrefix( prefix, systemNamespace );
	}
	
	/**
	 * Add new instance with table name to the RDF model.
	 * 
	 * @param table
	 * @throws R2RMapperException
	 */
	public Resource newTableInstance(String table)
			throws R2RMapperException {
		try{

			LOGGER.info("[RDF Model Generator] Add subject as new Class instance: " + table);
            Resource resource = ontModel.createResource(RDFUtils.createURI(prefix,table));
			resource.addProperty(RDF.type, RDFS.Class);
            resource.addProperty(RDFS.label, RDFUtils.createLiteral(ontModel,table,"STRING"));
            return resource;
		}catch(Exception ex){
			throw new R2RMapperException("[RDF Model Generator] New RDF instance initializing failed", ex);
		}
		
		
	}
	
	/**
	 * Add new instace to RDF model based on primary key in db. PK_
	 * 
	 * @param subj
	 * @param table
	 * @return
	 * @throws R2RMapperException
	 */
	public Resource newInstance(String subj, String table) 
			throws R2RMapperException {

		try{		
			Resource resource;
			Resource object;
			
			LOGGER.info("[RDF Model Generator] Add subject as new ClassMap instance: " + subj);
			resource = ontModel.createResource(prefix + ":"+table+"_PK_" + subj);
			//object = ontModel.createResource( systemNamespace + "TBL" + table);
			//resource.addProperty(RDFS.subClassOf, object);
			//Property property = ontModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
			//object = ontModel.createResource("sak:Import");
			//ontModel.add(resource, property, object);
			//TODO:Fix this
			return resource;
		}catch(Exception ex){
			throw new R2RMapperException("new RDF instance initializing failed", ex);
		}
		
		
	}
	
	/**
	 * Add a statement into RDF model
	 * 
	 * @param resource
	 * @param predicate
	 * @param obj
	 * @throws R2RMapperException
	 */
	public void addStatement(Resource resource, String predicate, String obj) 
			throws R2RMapperException {
	
		try{
			LOGGER.info("[RDF Model Generator] Add subject as new statement: " + obj);
			Property property;	
			
			if(obj != null){
				property = ontModel.createProperty(systemNamespace + predicate);
				ontModel.add(resource, property, obj);
			}else{
				LOGGER.debug("[RDF Model Generator] Skipping null column");
			}
			

		}catch(Exception ex){
			throw new R2RMapperException("[RDF Model Generator] New RDF statement initializing failed", ex);
		}


	}

    /**
     * Add new DatatypeProperty from column name to the RDF model.
     *
     * @param columnName
     * @param table
     * @param tableName
     * @throws R2RMapperException
     */
    public Resource addDatatypeProperty(String columnName, String columnType, Resource table, String tableName)
            throws R2RMapperException {
        try{
            LOGGER.info("[RDF Model Generator] Add column " +columnName+" as new DatatypeProperty");
            Resource resource = ontModel.createResource(RDFUtils.createURI(prefix,tableName+"_"+columnName));
            resource.addProperty(RDF.type, OWL.DatatypeProperty);
            resource.addProperty(RDFS.domain, table);
            resource.addProperty(RDFS.label, RDFUtils.createLiteral(ontModel,columnName,columnType));
            return resource;
        }catch(Exception ex){
            throw new R2RMapperException("[RDF Model Generator] New RDF instance initializing failed", ex);
        }


    }


    /**
     * Method for generating all custom prefixes
     *
     * @param prefix
     * @param url
     * @throws R2RMapperException
     */
    public void generateCustomPrefix(String prefix, String url){
        this.setPrefix(prefix);
        this.setBaseNamespace(url);
        this.setSystemNamespace(this.getBaseNamespace() + prefix + "#");
        this.addNsPrefix(prefix);
    }

    /**
     * Method for loading owl into model
     *
     * @param model
     * @throws R2RMapperException
     */
	public OntModel loadOwlModel(OntModel model) {
		FileInputStream inputStream = null;

		try {
			inputStream = new FileInputStream("ontology.owl");
			model.read(inputStream, null, "TURTLE");
		} catch (Throwable throwable) {
			System.err.println("Error reading file: "
					+ throwable.getClass().getName() + throwable.getMessage());
		} finally {
			try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Throwable throwable) {
				System.err.println("Error closing input file");
				throwable.printStackTrace();
				System.exit(4);
			}
		}
		return model;

	}


	
	

}
