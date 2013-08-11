package edu.itmo.ailab.semantic.r2rmapper.comparator;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * R2R Mapper. It is a free software
 *
 * Author: Ilya Semerhanov
 * Date: 11.08.13
 */
public class SorensenDice
{
    public static final Logger LOGGER=Logger.getLogger(SorensenDice.class);

    private class result
    {
        private String theWord;
        private int theCount;

        public result(String w, int c)
        {
            theWord = w;
            theCount = c;
        }

        public void setTheCount(int c)
        {
            theCount = c;
        }

        public int getTheCount()
        {
            return theCount;
        }
    }

    private List<result> results;

    public SorensenDice()
    {
        results = new ArrayList<result>();
    }
    public SorensenDice(String str, int n)
    {

    }

    public float computeSimilarity(String value1, String value2, int n)
    {
        List<result> res1 = processString(value1, n);
        List<result> res2 = processString(value2, n);
        int c = common(res1,res2);
        float res = (float)2*c/((float) res1.size() + (float) res2.size());
        return res;
    }

    private int common(List<result> One, List<result> Two)
    {
        int res = 0;

        for (int i = 0; i < One.size(); i++)
        {
            for (int j = 0; j < Two.size(); j++)
            {
                if (One.get(i).theWord.equalsIgnoreCase(Two.get(j).theWord)) res++;
            }
        }

        return res;
    }

    private List<result> processString(String c, int n)
    {
        List<result> t = new ArrayList<result>();

        String spacer = "";
        for (int i = 0; i < n-1; i++)
        {
            spacer = spacer + "%";
        }
        c = spacer + c + spacer;

        for (int i = 0; i < c.length(); i++)
        {
            if (i <= (c.length() - n))
            {
                if (contains(c.substring(i, n+i)) > 0)
                {
                    t.get(i).setTheCount(results.get(i).getTheCount()+1);
                }
                else
                {
                    t.add(new result(c.substring(i,n+i),1));
                }
            }
        }
        return t;
    }

    private int contains(String c)
    {
        for (int i = 0; i < results.size(); i++)
        {
            if (results.get(i).theWord.equalsIgnoreCase(c))
                return i;
        }
        return 0;
    }
}