package edu.itmo.ailab.semantic.r2rmapper.wi;

/**
 * R2R Mapper. It is a free software.
 * <p/>
 * <p/>
 * Author: Ilya
 * Date: 06.10.13
 */
import edu.itmo.ailab.semantic.r2rmapper.dbms.MatchingDBHandler;
import edu.itmo.ailab.semantic.r2rmapper.exceptions.R2RMapperException;
import edu.itmo.ailab.semantic.r2rmapper.properties.PropertyLoader;
import edu.itmo.ailab.semantic.r2rmapper.rdf.BasicMapper;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.*;
import java.sql.SQLException;
import java.util.Scanner;

@ManagedBean
@SessionScoped
public class MainBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }



    public void  saveTest(){
           test = "BlaBla";

    }

    public void  readTest() throws FileNotFoundException, R2RMapperException, ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException {
        String settingsPath = System.getProperty("jboss.server.data.dir") + "/settings.yaml";
        String configPath = System.getProperty("jboss.server.data.dir") + "/config.yaml";
        //InputStream input = new FileInputStream(new File(path));
        //String inputStreamString = new Scanner(input,"UTF-8").useDelimiter("\\A").next();
        //test = inputStreamString;
        PropertyLoader loader;
        BasicMapper bm;
        String outputFileNamePhase1 = "integrated_ontology_phase_1.owl";
        String outputFileNamePhase2 = "integrated_ontology_phase_2.owl";
        String ontologyFormat = "TURTLE";
        MatchingDBHandler.getInstance(settingsPath);
        MatchingDBHandler.connect();

        loader = new PropertyLoader(configPath);
        bm = new BasicMapper(loader.properties);

        MatchingDBHandler.flushDB();
        bm.createStructureMap();



    }
}