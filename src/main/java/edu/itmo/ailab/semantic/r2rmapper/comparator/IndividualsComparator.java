package edu.itmo.ailab.semantic.r2rmapper.comparator;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import edu.itmo.ailab.semantic.r2rmapper.dbms.RedisHandler;
import edu.itmo.ailab.semantic.r2rmapper.rdf.RDFModelGenerator;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Map.Entry;

/**
 * R2R Mapper. It is a free software.
 *
 * Author: Ilya Semerhanov
 * Date: 09.08.13
 */
public class IndividualsComparator {

    public static final Logger LOGGER=Logger.getLogger(IndividualsComparator.class);

    public String filePath;

    public IndividualsComparator(){

    }

    public void startComparison(RDFModelGenerator model){

        String table1 = "sys2_test";
        String table2 = "sys2_test2";
        String field1 = "name";
        String field2 = "name";
        String key1 = table1 + "_individuals";
        String key2 = table2 + "_individuals";
        Map<String,String> allIndividualsForKey1 = RedisHandler.getAllIndividuals(key1);
        Map<String,String> allIndividualsForKey2 = RedisHandler.getAllIndividuals(key2);
        String prop1 = RedisHandler.getPropertyName(table1, field1);
        String prop2 = RedisHandler.getPropertyName(table2, field2);
        OntModel ontModel = model.getOntModel();
        Boolean similarityFlag = false;

        for (Entry<String, String> entry : allIndividualsForKey1.entrySet())
        {
            for (Entry<String, String> entry2 : allIndividualsForKey2.entrySet())
            {
                LOGGER.debug("[Comparator] Compare " + entry.getKey() + " vs " + entry2.getKey());
                Statement st1 = ontModel.getIndividual(entry.getKey())
                                      .getProperty(ontModel.getProperty(prop1));
                Statement st2 = ontModel.getIndividual(entry2.getKey())
                        .getProperty(ontModel.getProperty(prop2));

                if (st1.getObject().isLiteral() && st2.getObject().isLiteral()) {
                    String val1 = st1.getLiteral().getLexicalForm().toString();
                    String val2 = st2.getLiteral().getLexicalForm().toString();
                    if(val1.length() >= 20 || val2.length() >= 20){
                        int k = DamerauLevenshtein.computeSimilarity(val1,val2); // for big strings
                        if(k <= 15){
                            similarityFlag = true;
                        }else{
                            similarityFlag = false;
                        }
                        LOGGER.debug("[Comparator] Compare values: " + val1 + " vs " + val2 + " Similarity: " + k+ " Similar?: " + similarityFlag);
                    }else{
                        float k = Tanimoto.computeSimilarity(val1, val2); //for short strings
                        if(k > 0.75 && k <= 1.0){
                            similarityFlag = true;
                        }else{
                            similarityFlag = false;
                        }
                        LOGGER.debug("[Comparator] Compare values: " + val1 + " vs " + val2 + " Similarity: " + k + " Similar?: " + similarityFlag);
                    }
                }

                if(similarityFlag){
                    Individual individual1 =  ontModel.getIndividual(entry.getKey());
                    Individual individual2 =  ontModel.getIndividual(entry2.getKey());
                    individual1.addProperty(OWL.sameAs,individual2);
                    individual2.addProperty(OWL.sameAs,individual1);
                }

            }
        }

    }


}
