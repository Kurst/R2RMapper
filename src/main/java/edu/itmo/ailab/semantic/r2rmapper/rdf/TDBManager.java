package edu.itmo.ailab.semantic.r2rmapper.rdf;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import org.apache.log4j.Logger;

/**
 * R2R Mapper. It is a free software.
 *
 * Jena TDB manager class
 * Author: Ilya Semerhanov
 * Date: 08.09.13
 *
 */

public class TDBManager {
    public static final Logger LOGGER = Logger.getLogger(TDBManager.class);

    private String pathToTDB;
    private Dataset dataset;
    private Model tdbModel;

    public TDBManager(String pathToTDB){
        this.pathToTDB = pathToTDB;
        dataset = TDBFactory.createDataset(pathToTDB);
        tdbModel = dataset.getDefaultModel();
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public Model getTdbModel() {
        return tdbModel;
    }

    public void setTdbModel(Model tdbModel) {
        this.tdbModel = tdbModel;
    }

    public void importFromFile(String filePath, String format){
        this.tdbModel.removeAll();
        FileManager.get().readModel(this.tdbModel, filePath, format);
        this.tdbModel.close();
    }

    public void showAllQuery(){
        String q = "select * where {?s ?p ?o}";
        Query query = QueryFactory.create(q);
        QueryExecution qexec = QueryExecutionFactory.create(query, this.dataset);
        ResultSet results = qexec.execSelect();
        ResultSetFormatter.out(results) ;

    }


}
