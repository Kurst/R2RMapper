package edu.itmo.ailab.semantic.r2rmapper.vocabulary;

/**
 * R2R Mapper. It is a free software.
 * R2R vacabulary
 *
 * Author: Ilya Semerhanov
 * Date: 11.08.13
 */


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;



public class R2R {

    public static final String NS = "http://ailab.ifmo.ru/r2r#";
    public static final String prefix = "r2r";
    public static final String hasSimilairtyName = "hasSimilarity";

    private static Model model = ModelFactory.createDefaultModel();

    public static final Property hasSimilarity = model.createProperty(NS, hasSimilairtyName);


}

