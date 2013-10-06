package edu.itmo.ailab.semantic.r2rmapper.rdf.impl;

import java.io.*;
import java.util.*;

import edu.itmo.ailab.semantic.r2rmapper.rdf.BasicMapper;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFWriter;

/**
 * R2R Mapper. It is a free software.
 *
 * ConsoleMapperImpl. Class that starts extraction.
 * Author: Ilya Semerhanov
 * Date: 06.08.13
 *
 */
public class ConsoleMapperImpl extends BasicMapper{

	public static final Logger LOGGER=Logger.getLogger(ConsoleMapperImpl.class);

    public ConsoleMapperImpl(List<Object> properties){
        this.setProperties(properties);
    }

    /**
	 * Prints RDF model in console
	 * @param format
	 * @throws FileNotFoundException 
	 */
	public void printModel(String format){
		
		LOGGER.info("[R2R Mapper] Printing output into console");
		
		OntModel ontModel = model.getOntModel();
		RDFWriter rdfWriter = ontModel.getWriter(format);
		rdfWriter.setProperty("width", String.valueOf(Integer.MAX_VALUE));
		rdfWriter.write(ontModel, System.out, null);
		System.out.println("\n");
	}
	
	/**
	 * Prints RDF model in file
	 * @param format
     * @param filename
	 * @throws IOException
	 */
	public void printModelToFile(String format, String filename, String path)
            throws IOException{
		
		LOGGER.info("[R2R Mapper] Printing output into file: " + path + "output/" + filename);
		
		new File(path + "/output").mkdirs();
		File file = new File(path + "output/" + filename);
		
		OntModel ontModel = model.getOntModel();
		RDFWriter rdfWriter = ontModel.getWriter(format);
		rdfWriter.setProperty("width", String.valueOf(Integer.MAX_VALUE));
		rdfWriter.write(ontModel, new FileOutputStream(file), null);
		System.out.println("\n");
		//transformToUTF8(new File("output/" + filename), "UTF-8", new File("output/" + "ut8_" + filename), "ascii");		
	}


}
