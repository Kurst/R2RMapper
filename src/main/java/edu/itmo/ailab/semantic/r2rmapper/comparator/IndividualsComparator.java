package edu.itmo.ailab.semantic.r2rmapper.comparator;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDFS;
import edu.itmo.ailab.semantic.r2rmapper.dbms.MatchingDBHandler;
import edu.itmo.ailab.semantic.r2rmapper.rdf.RDFModelGenerator;
import edu.itmo.ailab.semantic.r2rmapper.rdf.RDFUtils;
import edu.itmo.ailab.semantic.r2rmapper.vocabulary.R2R;
import edu.itmo.ailab.semantic.r2rmapper.vocabulary.SKOS;
import org.apache.log4j.Logger;


import java.util.HashSet;
import java.util.Map;
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

        try {
            ResIterator ri = ontModel.listSubjectsWithProperty(RDFUtils.getAnnotationProperty(ontModel,R2R.similarToPropertyShortUri));
            while(ri.hasNext()){
                Resource subjectWithProperty = ri.next();
                Resource className = subjectWithProperty.getPropertyResourceValue(RDFS.domain);
                Resource similarSubjectWithProperty = subjectWithProperty.getPropertyResourceValue(RDFUtils.getAnnotationProperty(ontModel,R2R.similarToPropertyShortUri));
                Resource similarClassName = similarSubjectWithProperty.getPropertyResourceValue(RDFS.domain);
                startComparison(ontModel,className.getURI(),similarClassName.getURI(),subjectWithProperty.getURI(),similarSubjectWithProperty.getURI());
            }
        }catch(NullPointerException e){
            LOGGER.error("[Comparator] similarTo AnnotationProperty was not found. Comparison failed.");
        }

    }

    private void startComparison(OntModel ontModel, String className1, String className2, String prop1, String prop2) {

        /*String table1 = "sak_film";
        String table2 = "sak_film";*/
        //String field1 = "name";
        //String field2 = "name";

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
        MatchingDBHandler.flushSimilarityDB();
        for (String entry1 : allIndividualsForKey1.keySet()) {
            st1 = RDFUtils.getStatement(ontModel, ontModel.getIndividual(entry1),prop1);
            for (String entry2 : allIndividualsForKey2.keySet()) {
                LOGGER.debug("[Comparator] Compare " + entry1 + " vs " + entry2);
                similarityLevel = "0";
                ngramSize = 2;
                st2 = RDFUtils.getStatement(ontModel, ontModel.getIndividual(entry2),prop2);

                if (st1.getObject().isLiteral() && st2.getObject().isLiteral()) {
                    String val1 = st1.getLiteral().getLexicalForm().toString();
                    String val2 = st2.getLiteral().getLexicalForm().toString();

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
