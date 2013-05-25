package edu.itmo.ailab.semantic.r2rmapper.rdf;

import com.hp.hpl.jena.ontology.OntModel;

import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;


public interface RDFModelGenerator {
	
	
	public OntModel createModel() throws R2RMapperException;
	
	/**
	 * Creates a new instance of the data class. 
	 * 
	 * @param subj The value which will be a class instance
	 * @throws R2RMapperException 
	 */
	public void newInstance(String subj) throws R2RMapperException;
	
	/**
	 * Creates a new RDF statement. subject - predicate - object 
	 * 
	 * @param subj Subject
	 * @param predicate Predicate
	 * @param obj Object
	 */
	public void addStatement(String subj, String predicate, String obj);
	
	public void createURI(String value);
		
	public void storeModel();

}
