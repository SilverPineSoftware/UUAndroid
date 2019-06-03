package uu.toolbox.core;

import android.app.Application;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;

import org.junit.Assert;
import org.junit.runner.RunWith;

/**
 * Unit tests for the UUParcel class
 */
@RunWith(AndroidJUnit4.class)
public class UUParcelTests extends ApplicationTestCase<Application>
{
    public UUParcelTests()
    {
        super(Application.class);
    }

    public void test_0000_serializeParcelNullInput() throws Exception
    {
        Bundle b = null;

        //noinspection ConstantConditions
        byte[] serialized = UUParcel.serializeParcel(b);
        Assert.assertNull("Expect null output from null input", serialized);
    }

    public void test_0001_dserializeParcelNullInput() throws Exception
    {
        //noinspection ConstantConditions
        Object serialized = UUParcel.deserializeParcelable(null, new byte[] { 0x01, 0x02 });
        Assert.assertNull("Expect null output from null creator", serialized);

        //noinspection ConstantConditions
        serialized = UUParcel.deserializeParcelable(Bundle.CREATOR, null);
        Assert.assertNull("Expect null output from null bytes", serialized);
    }

    public void test_0002_serializeAndDeserializeParcel() throws Exception
    {
        Bundle b = new Bundle();
        b.putString("Foo", "is to Bar");
        b.putString("Bar", "is to Baz");
        b.putString("Baz", "is to Foo");
        b.putInt("Int", 99);

        byte[] serialized = UUParcel.serializeParcel(b);
        Assert.assertNotNull("Expect non null output for serialize");

        Bundle deserialized = UUParcel.deserializeParcelable(Bundle.CREATOR, serialized);
        Assert.assertNotNull("Expect non null output for deserialize");

        //noinspection ConstantConditions
        Assert.assertArrayEquals(b.keySet().toArray(new String[0]), deserialized.keySet().toArray(new String[0]));

        //Assert.assertEquals("Excpect serialized-deserialize to yield same object", b, deserialized);
        for (String key : b.keySet())
        {
            Object expected = b.get(key);
            Object actual = deserialized.get(key);
            Assert.assertEquals("Expect same value for key: " + key, expected, actual);
        }

    }
}
