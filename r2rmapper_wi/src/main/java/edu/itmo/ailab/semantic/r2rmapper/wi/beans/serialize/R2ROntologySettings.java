package edu.itmo.ailab.semantic.r2rmapper.wi.beans.serialize;

import java.io.Serializable;

/**
 * R2R Mapper. It is a free software.
 * <p/>
 * <p/>
 * Author: Ilya
 * Date: 19.10.13
 */
public class R2ROntologySettings implements Serializable {


    private String structureOntologyName;
    private String dataOntologyName;
    private String ontologyFormat;
    private String outputFolder;


    public String getStructureOntologyName() {
        return structureOntologyName;
    }

    public void setStructureOntologyName(String structureOntologyName) {
        this.structureOntologyName = structureOntologyName;
    }

    public String getDataOntologyName() {
        return dataOntologyName;
    }

    public void setDataOntologyName(String dataOntologyName) {
        this.dataOntologyName = dataOntologyName;
    }

    public String getOntologyFormat() {
        return ontologyFormat;
    }

    public void setOntologyFormat(String ontologyFormat) {
        this.ontologyFormat = ontologyFormat;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }
}
