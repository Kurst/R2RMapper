package edu.itmo.ailab.semantic.r2rmapper.rdf;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.lang.*;

import org.apache.log4j.Logger;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;



import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;


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
			throw new R2RMapperException("new RDF model creation failed", ex);
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
	 * Add new instance with table name to the RDF model. With name TBL_
	 * 
	 * @param table
	 * @throws R2RMapperException
	 */
	public void newTableInstance(String table) 
			throws R2RMapperException {
		try{	
			Resource resource;
			Resource object;
			
			LOGGER.info("[RDF Model Generator] Add subject as new ClassMap instance: " + table);
			resource = ontModel.createResource(prefix + ":TBL_" + table);
			object = ontModel.createResource( "rdfs:Class");
			resource.addProperty(RDF.type, object);
			resource.addProperty(RDFS.label, table);

		}catch(Exception ex){
			throw new R2RMapperException("new RDF instance initializing failed", ex);
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
			Property property = ontModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
			object = ontModel.createResource("sak:Import");
			ontModel.add(resource, property, object);
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
			throw new R2RMapperException("new RDF statement initializing failed", ex);
		}


	}
	
	/*@Deprecated
	public void addTripple(String subj, String table) 
			throws R2RMapperException {
		
		try{		
			
			Resource resource = ontModel.createResource(prefix + ":TrippleMap_" + subj);	
			resource.addProperty(RDF.type, ontModel
											.createResource( rrNamespace + "TriplesMap"));
			
			Property logicalTableProperty = ontModel.createProperty(rrNamespace + "LogicalTable");
			Property tableNameProperty = ontModel.createProperty(rrNamespace + "tableName");
			
			resource.addProperty(logicalTableProperty, ontModel
												.createResource()
												.addProperty(tableNameProperty,table));
			
			Property subjectMapProperty = ontModel.createProperty(rrNamespace + "subjectMap");
			Property templateProperty = ontModel.createProperty(rrNamespace + "template");
			resource.addProperty(subjectMapProperty,ontModel
													.createResource()
													.addProperty(templateProperty, "http://localhost/" + table + "/" + subj));
			
			
		}catch(Exception ex){
			throw new R2RMapperException("new RDF instance initializing failed", ex);
		}
		
	}*/

	public void createURI(String value) {
		// TODO Create safe URI

	}
	
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
				inputStream.close();
			} catch (Throwable throwable) {
				System.err.println("Error closing input file");
				throwable.printStackTrace();
				System.exit(4);
			}
		}
		return model;

	}


	
	

}
