package edu.itmo.ailab.semantic.r2rmapper.comparator;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import edu.itmo.ailab.semantic.r2rmapper.dbms.MatchingDBHandler;
import edu.itmo.ailab.semantic.r2rmapper.rdf.RDFModelGenerator;
import edu.itmo.ailab.semantic.r2rmapper.vocabulary.SKOS;
import org.apache.log4j.Logger;


import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

/**
 * R2R Mapper. It is a free software.
 * <p/>
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

    public String filePath;

    public IndividualsComparator() {

    }

    public void startComparison(RDFModelGenerator model) {

        String table1 = "sys2_test";
        String table2 = "sys2_test2";
        /*String table1 = "sak_film";
        String table2 = "sak_film";*/
        String field1 = "name";
        String field2 = "name";
        String key1 = table1 + "_individuals";
        String key2 = table2 + "_individuals";
        Map<String, String> allIndividualsForKey1 = MatchingDBHandler.getAllIndividuals(key1);
        Map<String, String> allIndividualsForKey2 = MatchingDBHandler.getAllIndividuals(key2);
        String prop1 = MatchingDBHandler.getPropertyName(table1, field1);
        String prop2 = MatchingDBHandler.getPropertyName(table2, field2);
        OntModel ontModel = model.getOntModel();
        String similarityLevel = "0";
        int ngramSize = 2;
        Statement st1;
        Statement st2;
        MatchingDBHandler.flushSimilarityDB();

        for (String entry1 : allIndividualsForKey1.keySet()) {
            st1 = ontModel.getIndividual(entry1)
                    .getProperty(ontModel.getProperty(prop1));
            for (String entry2 : allIndividualsForKey2.keySet()) {
                LOGGER.debug("[Comparator] Compare " + entry1 + " vs " + entry2);
                similarityLevel = "0";
                ngramSize = 2;
                st2 = ontModel.getIndividual(entry2)
                        .getProperty(ontModel.getProperty(prop2));

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
