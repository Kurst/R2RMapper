package edu.itmo.ailab.semantic.r2rmapper.rdf;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;


public class RDFModelGeneratorImpl implements RDFModelGenerator {
	
	public static final Logger LOGGER=Logger.getLogger(RDFModelGeneratorImpl.class);
	
	private OntModel ontModel;
	
	private String modelName;
	
	private String rdfNamespace = "http://localhost/r2r/";
	
	public RDFModelGeneratorImpl(String modelName){
		
		this.modelName = modelName;
	}
	
	public OntModel createModel() 
			throws R2RMapperException {

		LOGGER.info("[RDF Model Generator] Creating new model: " + modelName);
		ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		Resource resource;
		Property property;
		Resource object;
		try{
			resource = ontModel.createResource("Table: " + modelName);
			property = ontModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
			object = ontModel.createResource("r2r:Table");
			ontModel.add(resource, property, object);
			return ontModel;
		}catch(Exception ex){
			throw new R2RMapperException("new RDF model creation failed", ex);
		}
	}
	
	public void newInstance(String subj) 
			throws R2RMapperException {
		// TODO Auto-generated method stub
		Resource resource;
		Property property;
		Resource object;

		try{
			LOGGER.info("[RDF Model Generator] Add subject as new instance: " + subj);
			//OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);	
			resource = ontModel.createResource("r2r: PK_" + subj);
			property = ontModel.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
			object = ontModel.createResource("owl:Thing");
			ontModel.add(resource, property, object);

			// Add the actual subject value as a label
			property = ontModel.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
			ontModel.add(resource, property, subj);

		}catch(Exception ex){
			throw new R2RMapperException("new RDF instance initializing failed", ex);
		}
		
		
	}

	public void addStatement(String subj, String predicate, String obj) {
		Resource resource;
		Property property;
		
		resource = ontModel.createResource("r2r: PK_" + subj);
		property = ontModel.createProperty("r2r:" + predicate);
		ontModel.add(resource, property, obj);


	}

	public void createURI(String value) {
		// TODO Auto-generated method stub

	}

	public void showModel(String format){
		ontModel.write(System.out,format);
	}
	
	public void showModel() {
		ontModel.write(System.out);
	}
	public void storeModel() {
		// TODO Auto-generated method stub

	}

	

}
