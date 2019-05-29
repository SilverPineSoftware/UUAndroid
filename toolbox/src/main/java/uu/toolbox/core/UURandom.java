package uu.toolbox.core;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * UURandom
 * Useful Utilities - UURandom contains helper methods for generating random data
 *
 * License:
 * You are free to use this code for whatever purposes you desire.
 * The only requirement is that you smile everytime you use it. :-)
 */
@SuppressWarnings("unused")
public final class UURandom
{
    private static SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a random byte array using java.security.SecureRandom
     *
     * @param length length of byte array to generate
     * @return byte array filled with data.
     * @see java.security.SecureRandom
     */
    @SuppressWarnings("unused")
    @NonNull
    public static byte[] randomBytes(final int length)
    {
        byte[] output = new byte[length];
        secureRandom.nextBytes(output);
        return output;
    }

    /**
     * Generates a random integer with an upper bound
     *
     * @param max maximum value
     * @return a random integer
     */
    @SuppressWarnings("unused")
    public static int randomInt(final int max)
    {
        return secureRandom.nextInt(max);
    }

    /**
     * Generates a random integer
     *
     * @return a random integer
     */
    @SuppressWarnings("unused")
    public static int randomInt()
    {
        return secureRandom.nextInt();
    }

    /**
     * Generates a random long
     *
     * @return a random long
     */
    @SuppressWarnings("unused")
    public static long randomLong()
    {
        return secureRandom.nextLong();
    }

    /**
     * Generates a random double
     *
     * @return a random double
     */
    @SuppressWarnings("unused")
    public static double randomDouble()
    {
        return secureRandom.nextDouble();
    }

    /**
     * Generates a random float
     *
     * @return a random float
     */
    @SuppressWarnings("unused")
    public static float randomFloat()
    {
        return secureRandom.nextFloat();
    }

    /**
     * Generates a random boolean
     *
     * @return a random boolean
     */
    @SuppressWarnings("unused")
    public static boolean randomBoolean()
    {
        return secureRandom.nextBoolean();
    }

    /**
     * Generates a random byte
     *
     * @return a random byte
     */
    @SuppressWarnings("unused")
    public static byte randomByte()
    {
        byte[] result = randomBytes(1);
        return result[0];
    }

    /**
     * Generates a random short
     *
     * @return a random short
     */
    @SuppressWarnings("unused")
    public static short randomShort()
    {
        byte[] result = randomBytes(2);
        ByteBuffer bb = ByteBuffer.wrap(result);
        return bb.getShort();
    }

    /**
     * Generates a random UUID and returns the string form
     *
     * @return a random string
     */
    public static String randomUuidString()
    {
        UUID uid = UUID.randomUUID();
        return uid.toString();
    }

    private UURandom()
    {
        // Static class only, do not allow instantiation
    }









    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Unit Testing Helpers
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @VisibleForTesting
    public static byte[] randomByteArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        byte[] a = new byte[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomByte();
        }

        return a;
    }

    @VisibleForTesting
    public static Byte[] randomByteObjArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        Byte[] a = new Byte[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomByte();
        }

        return a;
    }

    @VisibleForTesting
    public static short[] randomShortArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        short[] a = new short[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomShort();
        }

        return a;
    }

    @VisibleForTesting
    public static Short[] randomShortObjArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        Short[] a = new Short[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomShort();
        }

        return a;
    }

    @VisibleForTesting
    public static int[] randomIntArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        int[] a = new int[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomInt();
        }

        return a;
    }

    @VisibleForTesting
    public static Integer[] randomIntObjArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        Integer[] a = new Integer[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomInt();
        }

        return a;
    }

    @VisibleForTesting
    public static long[] randomLongArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        long[] a = new long[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomLong();
        }

        return a;
    }

    @VisibleForTesting
    public static Long[] randomLongObjArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        Long[] a = new Long[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomLong();
        }

        return a;
    }

    @VisibleForTesting
    public static float[] randomFloatArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        float[] a = new float[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomFloat();
        }

        return a;
    }

    @VisibleForTesting
    public static Float[] randomFloatObjArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        Float[] a = new Float[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomFloat();
        }

        return a;
    }

    @VisibleForTesting
    public static double[] randomDoubleArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        double[] a = new double[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomDouble();
        }

        return a;
    }

    @VisibleForTesting
    public static Double[] randomDoubleObjArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        Double[] a = new Double[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomDouble();
        }

        return a;
    }

    @VisibleForTesting
    public static boolean[] randomBooleanArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        boolean[] a = new boolean[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomBoolean();
        }

        return a;
    }

    @VisibleForTesting
    public static Boolean[] randomBooleanObjArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        Boolean[] a = new Boolean[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = UURandom.randomBoolean();
        }

        return a;
    }

    @VisibleForTesting
    public static char[] randomCharArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        char[] a = new char[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = (char)UURandom.randomByte();
        }

        return a;
    }

    @VisibleForTesting
    public static Character[] randomCharObjArray(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        Character[] a = new Character[length];
        for (int i = 0; i < length; i++)
        {
            a[i] = (char)UURandom.randomByte();
        }

        return a;
    }

    @VisibleForTesting
    public static String randomLetters(int maxLength)
    {
        int length = UURandom.randomInt(maxLength);
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length)
        {
            char c = (char)UURandom.randomByte();
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
            {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    @VisibleForTesting
    public static String randomString(int maxLength)
    {
        return new String(randomCharArray(maxLength));
    }

    @VisibleForTesting
    public static String randomWord(int maxLength)
    {
        return randomLetters(maxLength);
    }

    @VisibleForTesting
    public static String randomWords(int maxNumberOfWords, int maxWordLength)
    {
        StringBuilder sb = new StringBuilder();

        int words = randomInt(maxNumberOfWords);
        for (int i = 0; i < words; i++)
        {
            sb.append(randomWord(maxWordLength));
            sb.append(" ");
        }

        return sb.toString();
    }
}
