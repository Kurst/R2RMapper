package edu.itmo.ailab.semantic.r2rmapper.rdf;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import edu.itmo.ailab.semantic.r2rmapper.dbms.SQLDataType;
import edu.itmo.ailab.semantic.r2rmapper.xsd.XSDMapping;
import edu.itmo.ailab.semantic.r2rmapper.xsd.XSDType;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
