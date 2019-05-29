package uu.toolbox;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;

import uu.toolbox.core.UURandom;

public class UURandomTests
{
    @Test
    public void randomBytesNotNull() throws Exception
    {
        byte[] result = UURandom.randomBytes(10);
        Assert.assertNotNull("Expect random bytes to never be null", result);
    }

    @Test
    public void randomBytesNotSame() throws Exception
    {
        int rounds = 20;
        ArrayList<byte[]> results = new ArrayList<>();

        for (int i = 0; i < rounds; i++)
        {
            byte[] result = UURandom.randomBytes(10);
            Assert.assertNotNull("Expect random bytes to never be null", result);
            results.add(result);

            for (int j = 0; j < results.size(); j++)
            {
                if (i != j)
                {
                    byte[] compare = results.get(j);
                    Assert.assertNotSame("Expect calls to randomBytes to give different results", result, compare);
                }
            }
        }
    }

    @Test
    public void randomIntNotSame() throws Exception
    {
        int rounds = 20;
        int[] results = new int[rounds];

        for (int i = 0; i < rounds; i++)
        {
            int result = UURandom.randomInt();
            results[i] = result;

            for (int j = 0; j < rounds; j++)
            {
                if (i != j)
                {
                    int compare = results[j];
                    Assert.assertNotSame("Expect calls to randomInt to give different results", result, compare);
                }
            }
        }
    }

    @Test
    public void randomLongNotSame() throws Exception
    {
        int rounds = 20;
        long[] results = new long[rounds];

        for (int i = 0; i < rounds; i++)
        {
            long result = UURandom.randomLong();
            results[i] = result;

            for (int j = 0; j < rounds; j++)
            {
                if (i != j)
                {
                    long compare = results[j];
                    Assert.assertNotSame("Expect calls to randomLong to give different results", result, compare);
                }
            }
        }
    }

    @Test
    public void randomFloatNotSame() throws Exception
    {
        int rounds = 20;
        float[] results = new float[rounds];

        for (int i = 0; i < rounds; i++)
        {
            float result = UURandom.randomFloat();
            results[i] = result;

            for (int j = 0; j < rounds; j++)
            {
                if (i != j)
                {
                    float compare = results[j];
                    Assert.assertNotSame("Expect calls to randomFloat to give different results", result, compare);
                }
            }
        }
    }

    @Test
    public void randomDoubleNotSame() throws Exception
    {
        int rounds = 20;
        double[] results = new double[rounds];

        for (int i = 0; i < rounds; i++)
        {
            double result = UURandom.randomDouble();
            results[i] = result;

            for (int j = 0; j < rounds; j++)
            {
                if (i != j)
                {
                    double compare = results[j];
                    Assert.assertNotSame("Expect calls to randomDouble to give different results", result, compare);
                }
            }
        }
    }

    @Test
    public void randomShortNotSame() throws Exception
    {
        int rounds = 20;
        short[] results = new short[rounds];

        for (int i = 0; i < rounds; i++)
        {
            short result = UURandom.randomShort();
            results[i] = result;

            for (int j = 0; j < rounds; j++)
            {
                if (i != j)
                {
                    short compare = results[j];
                    Assert.assertNotSame("Expect calls to randomShort to give different results", result, compare);
                }
            }
        }
    }

    @Test
    public void randomByteNotSame() throws Exception
    {
        int rounds = 20;
        byte[] results = new byte[rounds];

        for (int i = 0; i < rounds; i++)
        {
            byte result = UURandom.randomByte();
            results[i] = result;

            for (int j = 0; j < rounds; j++)
            {
                if (i != j)
                {
                    byte compare = results[j];
                    Assert.assertNotSame("Expect calls to randomByte to give different results", result, compare);
                }
            }
        }
    }

    @Test
    public void randomIntWithMax() throws Exception
    {
        int rounds = 20;
        int max = 12345678;

        for (int i = 0; i < rounds; i++)
        {
            int result = UURandom.randomInt(max);
            Assert.assertTrue("Expect randomInt(bounded) to produce result less than max", result < max);
        }
    }
}
