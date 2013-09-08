package edu.itmo.ailab.semantic.r2rmapper.comparator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDFS;
import edu.itmo.ailab.semantic.r2rmapper.dbms.MatchingDBHandler;
import edu.itmo.ailab.semantic.r2rmapper.rdf.RDFUtils;
import edu.itmo.ailab.semantic.r2rmapper.vocabulary.R2R;
import edu.itmo.ailab.semantic.r2rmapper.vocabulary.SKOS;
import org.apache.log4j.Logger;


import java.util.*;
import java.util.Map.Entry;

/**
 * R2R Mapper. It is a free software.
 *
 * Similarity levels:
 * 0 - not similar
 * 1 - narrow match
 * 2 - close match
 * 3 - same
 * Author: Ilya Semerhanov
 * Date: 09.08.13
 */
public class IndividualsComparator {

    public static final Logger LOGGER = Logger.getLogger(IndividualsComparator.class);

    public IndividualsComparator() {

    }

    public void analyzeSimilarity(OntModel ontModel) {
        //TODO: intruduce hasDomainSimilairty property, together with hasSimilarity. In other words single comparision and multi comparison for a Class.
        LOGGER.info("[Comparator] Analyzing of similarity started");
        try {
            ResIterator ri = ontModel.listSubjectsWithProperty(RDFUtils.getAnnotationProperty(ontModel,R2R.similarToPropertyShortUri));
            Multimap<Resource,Resource> similarityMultiMap = HashMultimap.create();
            while(ri.hasNext()){
                Resource subjectWithProperty = ri.next();
                Resource className = subjectWithProperty.getPropertyResourceValue(RDFS.domain);
                similarityMultiMap.put(className,subjectWithProperty);
            }
            if(similarityMultiMap.size() > 0){
                Set<String> prop1= new HashSet();
                Set<String> prop2= new HashSet();
                for (Resource classKey : similarityMultiMap.keySet()) {
                    Resource similarClassName = null;
                    for(Resource property : similarityMultiMap.get(classKey)){
                        Resource similarSubjectWithProperty = property.getPropertyResourceValue(RDFUtils.getAnnotationProperty(ontModel,R2R.similarToPropertyShortUri));
                        similarClassName = similarSubjectWithProperty.getPropertyResourceValue(RDFS.domain);
                        prop1.add(property.getURI());
                        prop2.add(similarSubjectWithProperty.getURI());
                    }
                    startComparison(ontModel,classKey.getURI(),similarClassName.getURI(), prop1, prop2);

                }
            }else{
                LOGGER.info("[Comparator] Similarity properties were not found");
            }
        }catch(NullPointerException e){
            LOGGER.error("[Comparator] similarTo AnnotationProperty was not found. Comparison failed.");
        }

    }

    private void startComparison(OntModel ontModel, String className1, String className2, Set<String> prop1, Set<String> prop2) {

        try{
            String table1 = RDFUtils.parseClassTableNameFromURI(className1);
            String table2 = RDFUtils.parseClassTableNameFromURI(className2);
            String key1 = table1 + "_individuals";
            String key2 = table2 + "_individuals";
            Map<String, String> allIndividualsForKey1 = MatchingDBHandler.getAllIndividuals(key1);
            Map<String, String> allIndividualsForKey2 = MatchingDBHandler.getAllIndividuals(key2);
            String similarityLevel = "0";
            int ngramSize = 2;
            Statement st1;
            Statement st2;
            String val1 = "";
            String val2 = "";
            int counter = 0;

            MatchingDBHandler.flushSimilarityDB();
            for (String entry1 : allIndividualsForKey1.keySet()) {
                for (String entry2 : allIndividualsForKey2.keySet()) {
                    LOGGER.debug("[Comparator] Compare " + entry1 + " vs " + entry2);
                    similarityLevel = "0";
                    ngramSize = 2;

                    if(prop1 != null && prop2 != null){
                        counter = 0;
                        for(String property1 : prop1){
                            st1 = RDFUtils.getStatement(ontModel, ontModel.getIndividual(entry1),property1);
                            if (st1.getObject().isLiteral()){
                                if(counter == 0){
                                    val1 = st1.getLiteral().getLexicalForm();
                                }else{
                                    val1 = val1 + " " + st1.getLiteral().getLexicalForm();
                                }
                            }
                            counter++;
                        }
                        counter = 0;
                        for(String property2 : prop2){
                            st2 = RDFUtils.getStatement(ontModel, ontModel.getIndividual(entry2),property2);
                            if (st2.getObject().isLiteral()){
                                if(counter == 0){
                                    val2 = st2.getLiteral().getLexicalForm();
                                }else{
                                    val2 = val2 + " " + st2.getLiteral().getLexicalForm();
                                }
                            }
                            counter++;
                        }
                    }

                    if (!val1.isEmpty() && !val2.isEmpty()) {
                        if (val1.length() > 20 || val2.length() > 20) {
                            ngramSize = 3;
                        }
                        SorensenDice sd = new SorensenDice();
                        float k = sd.computeSimilarity(val1, val2, ngramSize);
                        if (k > 0.85) {
                            similarityLevel = "3";
                        }
                        if (k > 0.75 && k <= 0.85) {
                            similarityLevel = "2";
                        }
                        if (k > 0.6 && k <= 0.75) {
                            similarityLevel = "1";
                        }
                        if (k <= 0.6) {
                            similarityLevel = "0";
                        }

                        LOGGER.debug("[Comparator] Compare values: " + val1 + " vs " + val2 + " Similarity: " + k + " similarityLevel: " + similarityLevel);

                    }
                    if (Integer.parseInt(similarityLevel) > 0) {
                        MatchingDBHandler.addIndividualSimilarity(entry1, entry2, similarityLevel);
                    }

                }
            }
            provideSemanticProperties(ontModel);

        }catch (NullPointerException e){
            LOGGER.error("[Comparator] Comparison failed: " + e.getMessage());
        }


    }

    private void provideSemanticProperties(OntModel ontModel) {

        HashSet<String> keys = MatchingDBHandler.getAllSimilarIndividuals();
        Map<String, String> singleSimilarIndividualMap;
        Individual individual1;
        Individual individual2;
        int i = 0;
        for (String key : keys) {
            i++;
            System.out.println(i);
            singleSimilarIndividualMap = MatchingDBHandler.getSingleSimilarIndividual(key);
            for (Entry<String, String> singleSimilarIndividual : singleSimilarIndividualMap.entrySet()) {
                individual1 = ontModel.getIndividual(key);
                individual2 = ontModel.getIndividual(singleSimilarIndividual.getKey());
                if (singleSimilarIndividual.getValue().equals("3")) {
                    individual1.addProperty(SKOS.exactMatch, individual2);
                    individual2.addProperty(SKOS.exactMatch, individual1);
                }
                if (singleSimilarIndividual.getValue().equals("2")) {
                    individual1.addProperty(SKOS.closeMatch, individual2);
                    individual2.addProperty(SKOS.closeMatch, individual1);
                }
                if (singleSimilarIndividual.getValue().equals("1")) {
                    individual1.addProperty(SKOS.narrowMatch, individual2);
                    individual2.addProperty(SKOS.narrowMatch, individual1);
                }

            }

        }

    }

}
