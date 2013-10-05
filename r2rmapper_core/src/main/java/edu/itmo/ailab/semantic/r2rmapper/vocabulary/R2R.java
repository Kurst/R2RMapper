package edu.itmo.ailab.semantic.r2rmapper.vocabulary;

/**
 * R2R Mapper. It is a free software.
 * R2R vacabulary
 *
 * Author: Ilya Semerhanov
 * Date: 11.08.13
 */


import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;



public class R2R {

    public static final String NS = "http://ailab.ifmo.ru/r2r#";
    public static final String prefix = "r2r";
    public static final String similarToName = "similarTo";
    public static final String similarToPropertyShortUri = "r2r:similarTo";
    public static final String similarToManyName = "similarToMany";
    public static final String similarToManyPropertyShortUri = "r2r:similarToMany";

    private static OntModel ontModel = ModelFactory.createOntologyModel();

    public static final AnnotationProperty similarTo = ontModel.createAnnotationProperty(NS + similarToName);
    public static final AnnotationProperty similarToMany = ontModel.createAnnotationProperty(NS + similarToManyName);


}

