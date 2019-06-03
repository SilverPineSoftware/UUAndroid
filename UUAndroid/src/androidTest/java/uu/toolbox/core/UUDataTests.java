package uu.toolbox.core;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UUDataTests
{
    private void testSerializeException(Exception ex)
    {
        byte[] serialized = UUData.serializeObject(ex);
        Assert.assertNotNull(serialized);

        Exception deserialized = UUData.deserializeObject(Exception.class, serialized);
        Assert.assertNotNull(deserialized);
        Assert.assertEquals(ex.getMessage(), deserialized.getMessage());
        Assert.assertEquals(ex.getLocalizedMessage(), deserialized.getLocalizedMessage());
        Assert.assertEquals(ex.getClass(), deserialized.getClass());
    }

    @Test
    public void test_0000_testSerialize()
    {
        Exception exception = new Exception("Test exception");
        testSerializeException(exception);
    }

    @Test
    public void test_0001_testSerializeCaught()
    {
        Exception exception = new Exception("Test exception");

        try
        {
            throw exception;
        }
        catch (Exception ex)
        {
            testSerializeException(ex);
        }
    }

    @Test
    public void test_0002_testSerializeWithInner()
    {
        Exception exception = new Exception("Test exception", new RuntimeException("foobar"));
        testSerializeException(exception);
    }

    @Test
    public void test_0003_testSerializeNull()
    {
        byte[] result = UUData.serializeObject(null);
        Assert.assertNull(result);
    }
}
