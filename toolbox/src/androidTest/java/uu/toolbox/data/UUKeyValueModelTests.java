package uu.toolbox.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import uu.toolbox.core.UURandom;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UUKeyValueModelTests
{
    private Context getContext()
    {
        return InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void test_0000_testString() throws Exception
    {
        soSingleObjectTest(UURandom.randomString(10), String.class);
    }

    @Test
    public void test_0001_testInteger() throws Exception
    {
        soSingleObjectTest(57, Integer.class);
    }

    @Test
    public void test_0002_testLong() throws Exception
    {
        soSingleObjectTest(System.currentTimeMillis(), Long.class);
    }

    @Test
    public void test_0003_testDouble() throws Exception
    {
        soSingleObjectTest((double)57.0f, Double.class);
    }

    @Test
    public void test_0004_testFloat() throws Exception
    {
        soSingleObjectTest(90120.0f, Float.class);
    }

    @Test
    public void test_0005_testBlob() throws Exception
    {
        UUDatabase db = new TestDb(getContext());
        db.deleteDatabase();

        UUKeyValueModel cfg = new UUKeyValueModel();
        cfg.setKey("binary");
        cfg.setValue(UURandom.randomByteArray(20));

        UUKeyValueModel inserted = db.insertObject(UUKeyValueModel.class, cfg);
        Assert.assertNotNull(inserted);
        Assert.assertNotNull(inserted.getKey());
        Assert.assertNotNull(inserted.getValue());
        Assert.assertTrue(inserted.getValue() instanceof byte[]);
        Assert.assertEquals(cfg.getKey(), inserted.getKey());
        Assert.assertArrayEquals((byte[])cfg.getValue(), (byte[])inserted.getValue());
        checkConvenienceGettors(inserted, byte[].class);

        UUKeyValueModel query = db.querySingleObject(UUKeyValueModel.class, cfg.getPrimaryKeyWhereClause(), cfg.getPrimaryKeyWhereArgs(), null);
        Assert.assertNotNull(query);
        Assert.assertNotNull(query.getKey());
        Assert.assertNotNull(query.getValue());
        Assert.assertTrue(query.getValue() instanceof byte[]);
        Assert.assertEquals(cfg.getKey(), query.getKey());
        Assert.assertArrayEquals((byte[])cfg.getValue(), (byte[])query.getValue());
        checkConvenienceGettors(query, byte[].class);

        db.deleteObject(UUKeyValueModel.class, cfg);

        query = db.querySingleObject(UUKeyValueModel.class, cfg.getPrimaryKeyWhereClause(), cfg.getPrimaryKeyWhereArgs(), null);
        Assert.assertNull(query);
    }

    @Test
    public void test_0006_testShort() throws Exception
    {
        soSingleObjectTest((short)44, Short.class);
    }

    @Test
    public void test_0007_testByte() throws Exception
    {
        soSingleObjectTest((byte)99, Byte.class);
    }

    @Test
    public void test_0008_testBoolean() throws Exception
    {
        soSingleObjectTest(true, Boolean.class);
        soSingleObjectTest(false, Boolean.class);
    }

    @Test
    public void test_0009_testMultiple() throws Exception
    {
        UUDatabase db = new TestDb(getContext());
        db.deleteDatabase();

        UUKeyValueModel one = new UUKeyValueModel("one", "Hello");
        UUKeyValueModel two = new UUKeyValueModel("two", "World");
        UUKeyValueModel three = new UUKeyValueModel("three", "Useful Utilities!");

        db.insertObject(UUKeyValueModel.class, one);
        Assert.assertEquals(1, db.count(UUKeyValueModel.class));

        db.insertObject(UUKeyValueModel.class, two);
        Assert.assertEquals(2, db.count(UUKeyValueModel.class));

        db.insertObject(UUKeyValueModel.class, three);
        Assert.assertEquals(3, db.count(UUKeyValueModel.class));

        UUKeyValueModel oneFetch = db.querySingleObject(UUKeyValueModel.class, one.getPrimaryKeyWhereClause(), one.getPrimaryKeyWhereArgs(), null);
        Assert.assertNotNull(oneFetch);
        Assert.assertNotNull(oneFetch.getKey());
        Assert.assertNotNull(oneFetch.getValue());
        Assert.assertEquals(one.getKey(), oneFetch.getKey());
        Assert.assertEquals(one.getValue(), oneFetch.getValue());

        UUKeyValueModel twoFetch = db.querySingleObject(UUKeyValueModel.class, two.getPrimaryKeyWhereClause(), two.getPrimaryKeyWhereArgs(), null);
        Assert.assertNotNull(twoFetch);
        Assert.assertNotNull(twoFetch.getKey());
        Assert.assertNotNull(twoFetch.getValue());
        Assert.assertEquals(two.getKey(), twoFetch.getKey());
        Assert.assertEquals(two.getValue(), twoFetch.getValue());

        UUKeyValueModel threeFetch = db.querySingleObject(UUKeyValueModel.class, three.getPrimaryKeyWhereClause(), three.getPrimaryKeyWhereArgs(), null);
        Assert.assertNotNull(threeFetch);
        Assert.assertNotNull(threeFetch.getKey());
        Assert.assertNotNull(threeFetch.getValue());
        Assert.assertEquals(three.getKey(), threeFetch.getKey());
        Assert.assertEquals(three.getValue(), threeFetch.getValue());

        db.deleteObject(UUKeyValueModel.class, one);
        Assert.assertEquals(2, db.count(UUKeyValueModel.class));

        db.deleteObject(UUKeyValueModel.class, two);
        Assert.assertEquals(1, db.count(UUKeyValueModel.class));

        db.deleteObject(UUKeyValueModel.class, three);
        Assert.assertEquals(0, db.count(UUKeyValueModel.class));

        oneFetch = db.querySingleObject(UUKeyValueModel.class, one.getPrimaryKeyWhereClause(), one.getPrimaryKeyWhereArgs(), null);
        Assert.assertNull(oneFetch);

        twoFetch = db.querySingleObject(UUKeyValueModel.class, two.getPrimaryKeyWhereClause(), two.getPrimaryKeyWhereArgs(), null);
        Assert.assertNull(twoFetch);

        threeFetch = db.querySingleObject(UUKeyValueModel.class, three.getPrimaryKeyWhereClause(), three.getPrimaryKeyWhereArgs(), null);
        Assert.assertNull(threeFetch);
    }

    private void soSingleObjectTest(@NonNull Object value, @NonNull Class<?> expectedType) throws Exception
    {
        UUDatabase db = new TestDb(getContext());
        db.deleteDatabase();

        UUKeyValueModel cfg = new UUKeyValueModel();
        cfg.setKey(UURandom.randomLetters(10));
        cfg.setValue(value);

        UUKeyValueModel inserted = db.insertObject(UUKeyValueModel.class, cfg);
        Assert.assertNotNull(inserted);
        Assert.assertNotNull(inserted.getKey());
        Assert.assertNotNull(inserted.getValue());
        Assert.assertTrue(inserted.getValue().getClass() == expectedType);
        Assert.assertEquals(cfg.getKey(), inserted.getKey());
        Assert.assertEquals(cfg.getValue(), inserted.getValue());
        checkConvenienceGettors(inserted, expectedType);

        UUKeyValueModel query = db.querySingleObject(UUKeyValueModel.class, cfg.getPrimaryKeyWhereClause(), cfg.getPrimaryKeyWhereArgs(), null);
        Assert.assertNotNull(query);
        Assert.assertNotNull(query.getKey());
        Assert.assertNotNull(query.getValue());
        Assert.assertTrue(query.getValue().getClass() == expectedType);
        Assert.assertEquals(cfg.getKey(), query.getKey());
        Assert.assertEquals(cfg.getValue(), query.getValue());
        checkConvenienceGettors(query, expectedType);

        db.deleteObject(UUKeyValueModel.class, cfg);

        query = db.querySingleObject(UUKeyValueModel.class, cfg.getPrimaryKeyWhereClause(), cfg.getPrimaryKeyWhereArgs(), null);
        Assert.assertNull(query);
    }

    private void checkConvenienceGettors(@NonNull UUKeyValueModel obj, @NonNull Class<?> expectedType) throws Exception
    {
        String stringVal = obj.getValueAsString();
        byte[] byteArrayVal = obj.getValueAsBlob();
        Long longVal = obj.getValueAsLong();
        Integer intVal = obj.getValueAsInteger();
        Short shortVal = obj.getValueAsShort();
        Byte byteVal = obj.getValueAsByte();
        Double doubleVal = obj.getValueAsDouble();
        Float floatVal = obj.getValueAsFloat();
        Boolean booleanVal = obj.getValueAsBoolean();

        checkConvenienceGettorValue(stringVal, expectedType, String.class);
        checkConvenienceGettorValue(byteArrayVal, expectedType, byte[].class);
        checkConvenienceGettorValue(longVal, expectedType, Long.class);
        checkConvenienceGettorValue(intVal, expectedType, Integer.class);
        checkConvenienceGettorValue(shortVal, expectedType, Short.class);
        checkConvenienceGettorValue(byteVal, expectedType, Byte.class);
        checkConvenienceGettorValue(doubleVal, expectedType, Double.class);
        checkConvenienceGettorValue(floatVal, expectedType, Float.class);
        checkConvenienceGettorValue(booleanVal, expectedType, Boolean.class);
    }

    private void checkConvenienceGettorValue(@Nullable Object value, @NonNull Class<?> expectedType, @NonNull Class<?> gettorType) throws Exception
    {
        if (expectedType == gettorType)
        {
            Assert.assertNotNull(value);
        }
        else
        {
            Assert.assertNull(value);
        }
    }


    @UUSqlDatabase(models =
    {
            UUKeyValueModel.class
    })
    class TestDbSchema implements UUDatabaseDefinition
    {

    }

    class TestDb extends UUDefaultDatabase
    {
        TestDb(@NonNull final Context context)
        {
            super(context, new TestDbSchema());
        }

    }
}


