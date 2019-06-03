package uu.toolbox;

import android.support.v4.util.Pair;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;

import uu.toolbox.core.UUString;

public class UUStringTests
{
    @Test
    public void byteToAscii() throws Exception
    {
        String input = "The quick brown fox jumps over the lazy dog.";

        byte[] output = UUString.stringToAsciiBytes(input);

        String check = UUString.byteToAsciiString(output);

        Assert.assertEquals("Expect conversion to and from bytes yields the same input", input, check);

        byte[] buffer = new byte[output.length + 5];
        System.arraycopy(output, 0, buffer, 0, output.length);

        String check2 = UUString.byteToAsciiString(buffer).trim();// .replace("\0", "");

        Assert.assertEquals("Expect conversion to and from bytes with extra zeros yields the same input", input, check2);
    }

    @Test
    public void toSnakeCaseTest()
    {
        ArrayList<Pair<String, String>> testData = new ArrayList<>();
        testData.add(new Pair<>("foo", "foo"));
        testData.add(new Pair<>("Foo", "foo"));
        testData.add(new Pair<>("fooBar", "foo_bar"));
        testData.add(new Pair<>("FooBar", "foo_bar"));
        testData.add(new Pair<>("FOoBar", "foo_bar"));
        testData.add(new Pair<>("ThisIsCamelCase", "this_is_camel_case"));

        for (Pair<String,String> td : testData)
        {
            String input = td.first;
            String expected = td.second;
            String actual = UUString.toSnakeCase(input);
            Assert.assertEquals(expected, actual);
        }
    }

    @Test
    public void truncateStringTest()
    {
        ArrayList<Object[]> testData = new ArrayList<>();
        testData.add(new Object[] { "foo",  4, "foo" });
        testData.add(new Object[] { "foo",  3, "foo" });
        testData.add(new Object[] { "foo",  2, "fo" });

        for (Object[] td : testData)
        {
            String input = (String)td[0];
            Integer length = (Integer)td[1];
            String expected = (String)td[2];
            String actual = UUString.truncateString(input, length);
            Assert.assertEquals(expected, actual);
        }
    }
}
