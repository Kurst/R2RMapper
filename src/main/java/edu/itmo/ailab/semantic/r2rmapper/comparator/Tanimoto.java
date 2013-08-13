package edu.itmo.ailab.semantic.r2rmapper.comparator;

import org.apache.log4j.Logger;

/**
 * R2R Mapper. It is a free software.
 *
 * Author: Ilya Semerhanov
 * Date: 10.08.13
 */
public class Tanimoto {

    public static final Logger LOGGER=Logger.getLogger(Tanimoto.class);

    /**
     * Compute Tanimoto factor
     *
     * @param value1
     * @param value2
     */
    public static float computeSimilarity(String value1, String value2) {
        float res;
        int a = value1.length();
        int b = value2.length();
        int c = 0;
        for(Character ch1 : value1.toLowerCase().toCharArray()){
            for(Character ch2 : value2.toLowerCase().toCharArray()){
                if(ch1.charValue() == ch2.charValue()){
                    c = c + 1;
                    break;
                }
            }
        }
        res = (float) c / ((float) a + (float) b - (float) c);
        return res;
    }
}
