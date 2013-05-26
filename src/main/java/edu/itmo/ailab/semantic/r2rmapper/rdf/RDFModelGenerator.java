package edu.itmo.ailab.semantic.r2rmapper.rdf;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;



import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;


public class RDFModelGenerator{
	
	public static final Logger LOGGER=Logger.getLogger(RDFModelGenerator.class);
	
	private OntModel ontModel;
	
	private String modelName;
	
	public String baseNamespace = "http://localhost/";
	
	protected String d2rqNamespace = "http://d2rq.org/terms/d2rq#";
		
	public String defaultPrefix = "r2r";
	
	
	public RDFModelGenerator(String modelName, String prefix){
		
		this.modelName = modelName;
		this.defaultPrefix = prefix;
		this.baseNamespace = baseNamespace + prefix + "#";
	}
	
	
	public String getDefaultNamespace() {
		return baseNamespace;
	}

	public void setDefaultNamespace(String baseNamespace) {
		this.baseNamespace = baseNamespace;
	}
	
	public OntModel createModel() 
			throws R2RMapperException {

		
		try{
			LOGGER.info("[RDF Model Generator] Creating new RDF model: " + modelName);		
			
			ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);		
			ontModel.setNsPrefix( defaultPrefix, baseNamespace );
			ontModel.setNsPrefix( "d2rq", d2rqNamespace );
			return ontModel;
		}catch(Exception ex){
			throw new R2RMapperException("new RDF model creation failed", ex);
		}
	}
	
	
	public void newClassMapInstance(String table) 
			throws R2RMapperException {

		try{	
			Resource resource;
			Resource object;
			
			LOGGER.info("[RDF Model Generator] Add subject as new ClassMap instance: " + table);
			resource = ontModel.createResource(defaultPrefix + ":TBL_" + table);
			object = ontModel.createResource( d2rqNamespace + "ClassMap");
			resource.addProperty(RDF.type, object);
			resource.addProperty(RDFS.label, table);

		}catch(Exception ex){
			throw new R2RMapperException("new RDF instance initializing failed", ex);
		}
		
		
	}
	
	public Resource newInstance(String subj, String table) 
			throws R2RMapperException {

		try{		
			Resource resource;
			Resource object;
						
			LOGGER.info("[RDF Model Generator] Add subject as new ClassMap instance: " + subj);
			resource = ontModel.createResource(defaultPrefix + ":"+table+"_PK_" + subj);
			object = ontModel.createResource( baseNamespace + "TBL_" + table);
			resource.addProperty(RDFS.subClassOf, object);
			
			return resource;
		}catch(Exception ex){
			throw new R2RMapperException("new RDF instance initializing failed", ex);
		}
		
		
	}

	public void addStatement(Resource resource, String predicate, String obj) 
			throws R2RMapperException {
	
		try{	
			LOGGER.info("[RDF Model Generator] Add subject as new statement: " + obj);
			Property property;	
			property = ontModel.createProperty(baseNamespace + predicate);
			ontModel.add(resource, property, obj);

		}catch(Exception ex){
			throw new R2RMapperException("new RDF statement initializing failed", ex);
		}


	}

	public void createURI(String value) {
		// TODO Auto-generated method stub

	}

	public void showModel(String format){

		RDFWriter rdfWriter = ontModel.getWriter(format);
		
		rdfWriter.setProperty("width", String.valueOf(Integer.MAX_VALUE));
		rdfWriter.write(ontModel, System.out, null);
		System.out.println("\n");
		
	}
	
	public void showModel() {
		ontModel.write(System.out);
	}
	public void storeModel() {
		// TODO Auto-generated method stub

	}
	

}
