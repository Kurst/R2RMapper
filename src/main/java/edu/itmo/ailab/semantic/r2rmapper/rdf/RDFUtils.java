package edu.itmo.ailab.semantic.r2rmapper.rdf;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import edu.itmo.ailab.semantic.r2rmapper.dbms.SQLDataType;
import edu.itmo.ailab.semantic.r2rmapper.xsd.XSDMapping;
import edu.itmo.ailab.semantic.r2rmapper.xsd.XSDType;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * R2R Mapper. It is a free software.
 *
 * Usefull RDF utilities
 * Author: Ilya Semerhanov
 * Date: 06.08.13
 *
 */
public class RDFUtils {


    /**
     * Create safe URI from prefix and name
     *
     * @param prefix
     * @param name
     */
    public static String createURI(String prefix, String name) {
        String res = prefix + ":" + name;
        return res.replaceAll(" ", "_");
    }

    /**
     * Create Literal
     *
     * @param name
     * @param type
     */
    public static Literal createLiteral(OntModel model, String name, String type)
            throws ParseException {
        XSDType xsdtype = XSDMapping.getXSDType(SQLDataType.getTypeFromString(type.toUpperCase()));
        if(xsdtype == XSDType.DATETIME){
           try{
               Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(name);
               SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //Converting to xsd timestamp format
               name = df.format(date);
           }catch(Exception e){
               xsdtype = XSDType.STRING;
           }
        }
        Literal literal = model.createTypedLiteral(name,XSDType.XSD_NAMESPACE + xsdtype);
        return literal;
    }

    /**
     * Create Datatype Property from short name or long name
     *
     * @param ontModel
     * @param propertyShortName
     *
     */
    public static DatatypeProperty getDatatypeProperty(OntModel ontModel, String propertyShortName){
        DatatypeProperty property;
        property = ontModel.getDatatypeProperty(propertyShortName);
        if(property == null){
            String nsPrefix = propertyShortName.substring(0,propertyShortName.indexOf(":"));
            String instanceName = propertyShortName.substring(propertyShortName.indexOf(":")+1);
            String propertyLongName = ontModel.getNsPrefixURI(nsPrefix) + instanceName;
            property = ontModel.getDatatypeProperty(propertyLongName);
        }
        return property;
    }

    /**
     * Create Property from short name or long name
     *
     * @param ontModel
     * @param propertyShortName
     *
     */
    public static Property getProperty(OntModel ontModel, String propertyShortName){
        Property property;
        property = ontModel.getProperty(propertyShortName);
        if(property == null){
            String nsPrefix = propertyShortName.substring(0,propertyShortName.indexOf(":"));
            String instanceName = propertyShortName.substring(propertyShortName.indexOf(":")+1);
            String propertyLongName = ontModel.getNsPrefixURI(nsPrefix) + instanceName;
            property = ontModel.getProperty(propertyLongName);
        }
        return property;
    }

    /**
     * Create Statement from short name or long name
     *
     * @param ontModel
     * @param propertyShortName
     *
     */
    public static Statement getStatement(OntModel ontModel, Individual i, String propertyShortName){
        Property property = getProperty(ontModel, propertyShortName);
        Statement statement;
        statement = i.getProperty(property);
        if(statement == null){
            String nsPrefix = propertyShortName.substring(0,propertyShortName.indexOf(":"));
            String instanceName = propertyShortName.substring(propertyShortName.indexOf(":")+1);
            String propertyLongName = ontModel.getNsPrefixURI(nsPrefix) + instanceName;
            statement = i.getProperty(ontModel.getProperty(propertyLongName));
        }
        return statement;
    }

    /**
     * Parse CallName from URI
     *
     * @param className
     */
    public static String parseClassTableNameFromURI(String className){
        String res = null;
        Pattern regex = Pattern.compile("^([^http:].*):(.*)$");
        Matcher m = regex.matcher(className);
        if(m.matches()){
            if (m.find()) {
                String systemName = m.group(1);
                String tableName = m.group(2);
                res = systemName + "_" + tableName;
            }
        }else{
            Pattern regex2 = Pattern.compile("^(.*)#(.*)$");
            String name = className.substring(className.lastIndexOf("/")+1);
            Matcher m2 = regex2.matcher(name);
            if (m2.find()) {
                String systemName = m2.group(1);
                String tableName = m2.group(2);
                res = systemName + "_" + tableName;
            }
        }
        return res;
    }


    /**
     * Transform file encoding
     *
     * @param source
     * @param srcEncoding
     * @param target
     * @param tgtEncoding
     * @throws java.io.IOException
     */
    public static void transformToUTF8(File source, String srcEncoding, File target, String tgtEncoding)
            throws IOException {
        BufferedReader br = null;
        BufferedWriter bw = null;
        try{
            br = new BufferedReader(new InputStreamReader(new FileInputStream(source),srcEncoding));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), tgtEncoding));
            char[] buffer = new char[16384];
            int read;
            while ((read = br.read(buffer)) != -1)
                bw.write(buffer, 0, read);
        } finally {
            try {
                if (br != null)
                    br.close();
            } finally {
                if (bw != null)
                    bw.close();
            }
        }
    }
}
