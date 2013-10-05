package edu.itmo.ailab.semantic.r2rmapper.comparator;

import org.apache.log4j.Logger;

/**
 * R2R Mapper. It is a free software.
 *
 * Author: Ilya Semerhanov
 * Date: 10.08.13
 */
public class DamerauLevenshtein
{
    public static final Logger LOGGER=Logger.getLogger(DamerauLevenshtein.class);

    private static int[][] matrix;

    public static int computeSimilarity(String value1, String value2) {
        if ((value1.length() > 0 || !value1.isEmpty()) || (value2.length() > 00 || !value2.isEmpty()))
        {
            prepareMatrix(value1,value2);
            return matrix[value1.length()][value2.length()];
        }
        return 100;
    }

    private static void prepareMatrix(String compOne, String compTwo)
    {
        int cost = -1;
        int del, sub, ins;

        matrix = new int[compOne.length()+1][compTwo.length()+1];

        for (int i = 0; i <= compOne.length(); i++)
        {
            matrix[i][0] = i;
        }

        for (int i = 0; i <= compTwo.length(); i++)
        {
            matrix[0][i] = i;
        }

        for (int i = 1; i <= compOne.length(); i++)
        {
            for (int j = 1; j <= compTwo.length(); j++)
            {
                if (compOne.charAt(i-1) == compTwo.charAt(j-1))
                {
                    cost = 0;
                }
                else
                {
                    cost = 1;
                }

                del = matrix[i-1][j]+1;
                ins = matrix[i][j-1]+1;
                sub = matrix[i-1][j-1]+cost;

                matrix[i][j] = min(del,ins,sub);

                if ((i > 1) && (j > 1) && (compOne.charAt(i-1) == compTwo.charAt(j-2)) && (compOne.charAt(i-2) == compTwo.charAt(j-1)))
                {
                    matrix[i][j] = min(matrix[i][j], matrix[i-2][j-2]+cost);
                }
            }
        }

    }

    private static int min(int d, int i, int s)
    {
        int m = Integer.MAX_VALUE;

        if (d < m) m = d;
        if (i < m) m = i;
        if (s < m) m = s;

        return m;
    }

    private static int min(int d, int t)
    {
        int m = Integer.MAX_VALUE;

        if (d < m) m = d;
        if (t < m) m = t;

        return m;
    }
}