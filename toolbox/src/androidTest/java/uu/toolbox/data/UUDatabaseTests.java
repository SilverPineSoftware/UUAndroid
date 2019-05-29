package uu.toolbox.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Locale;

import uu.toolbox.logging.UULog;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UUDatabaseTests
{
    private static final String TEST_DATA_MODEL_TABLE_NAME = "uutest_data_model";
    private static final String DATA_MODEL_WITH_COMPOUND_KEY_TABLE_NAME = "uu_data_model_with_compound_key";
    private static final String COMPLEX_DATA_MODEL_TABLE_NAME = "uu_complex_test_model";

    static class UUColumnDefinition
    {
        int cid;
        String name;
        String type;
        boolean notNull;
        Object defaultValue;
        int pkIndex;

        void fillFromCursor(@NonNull Cursor cursor)
        {
            cid = UUCursor.safeGetInt(cursor, "cid");
            name = UUCursor.safeGetString(cursor, "name");
            type = UUCursor.safeGetString(cursor, "type");
            notNull = UUCursor.safeGetBoolean(cursor, "notnull");
            defaultValue = UUCursor.safeGetString(cursor, "dflt_value");
            pkIndex = UUCursor.safeGetInt(cursor, "pk");
        }
    }

    private ArrayList<UUColumnDefinition> getTableSchema(
            @NonNull final UUSQLiteDatabase db,
            @NonNull final String tableName)
    {
        String sql = String.format(Locale.US, "PRAGMA table_info(%s);", tableName);

        ArrayList<UUColumnDefinition> results = new ArrayList<>();

        Cursor c = null;

        try
        {
            c = db.rawQuery(sql, null);

            while (c.moveToNext())
            {
                UUColumnDefinition obj = new UUColumnDefinition();
                obj.fillFromCursor(c);
                results.add(obj);
            }
        }
        catch (Exception ex)
        {
            Assert.fail("Caught an exception, ex: " + ex.toString());
        }
        finally
        {
            try
            {
                if (c != null)
                {
                    c.close();
                }
            }
            catch (Exception ex2)
            {
                Assert.fail("Caught an exception, ex2: " + ex2.toString());
            }
        }

        return results;
    }

    private Context getContext()
    {
        return InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void test_0000_initialCreate()
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_ONE;
        ctx.deleteDatabase(UUTestDatabase.NAME);


        UUTestDatabase db = new UUTestDatabase(ctx);

        ArrayList<String> tables = db.listTableNames();
        Assert.assertNotNull("Expect table names list to be non null", tables);

        // At this point our data model only has one table, but Android internally adds
        // 'android_metadata' and if any auto increment tables are used, a 'sqlite_sequence' table.
        Assert.assertTrue("Expect one table", tables.size() >= 1);

        for (String table: tables)
        {
            if ("android_metadata".equals(table) ||
                "sqlite_sequence".equals(table))
            {
                continue;
            }

            Assert.assertEquals("Expect one table name to be correct", TEST_DATA_MODEL_TABLE_NAME, table);

            getTableSchema(db.getReadOnlyDatabase(), table);
        }
    }

    @Test
    public void test_0001_upgradeToVersionTwo() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_TWO;

        UUTestDatabase db = new UUTestDatabase(ctx);

        ArrayList<String> tables = db.listTableNames();
        Assert.assertNotNull("Expect table names list to be non null", tables);

        // At this point our data model only has one table, but Android internally adds
        // 'android_metadata' and if any auto increment tables are used, a 'sqlite_sequence' table.
        Assert.assertTrue("Expect more than one table", tables.size() >= 1);

        Assert.assertTrue("Expect tables to contain test data model", tables.contains(TEST_DATA_MODEL_TABLE_NAME));
        Assert.assertTrue("Expect tables to contain compound primary key test data model", tables.contains(DATA_MODEL_WITH_COMPOUND_KEY_TABLE_NAME));

        getTableSchema(db.getReadOnlyDatabase(), TEST_DATA_MODEL_TABLE_NAME);
        getTableSchema(db.getReadOnlyDatabase(), DATA_MODEL_WITH_COMPOUND_KEY_TABLE_NAME);
    }

    @Test
    public void test_0002_downgradeToVersionOne() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_ONE;

        UUTestDatabase db = new UUTestDatabase(ctx);

        ArrayList<String> tables = db.listTableNames();
        Assert.assertNotNull("Expect table names list to be non null", tables);

        // At this point our data model only has one table, but Android internally adds
        // 'android_metadata' and if any auto increment tables are used, a 'sqlite_sequence' table.
        Assert.assertTrue("Expect more than one table", tables.size() >= 1);

        Assert.assertTrue("Expect tables to contain test data model", tables.contains(new UUTestDataModel().getTableName()));
    }

    @Test
    public void test_0003_addObjectTwice() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_TWO;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        UUDataModelWithCompoundKey m = new UUDataModelWithCompoundKey();
        m.c1 = "one";
        m.c2 = "two";
        m.data = "This is some data";

        UUDataModelWithCompoundKey added = db.insertObject(UUDataModelWithCompoundKey.class, m);
        Assert.assertNotNull("Expect add to succeed when object does not exist");
        Assert.assertEquals(m.c1, added.c1);
        Assert.assertEquals(m.c2, added.c2);
        Assert.assertEquals(m.data, added.data);

        UUDataModelWithCompoundKey added2 = db.insertObject(UUDataModelWithCompoundKey.class, m);
        Assert.assertNull("Expect add to fail when object exists", added2);
    }

    @Test
    public void test_0004_createWithComplexDataModels() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_THREE;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        ArrayList<UUColumnDefinition> schema = getTableSchema(db.getReadOnlyDatabase(), COMPLEX_DATA_MODEL_TABLE_NAME);
        for (UUColumnDefinition cd : schema)
        {
            UULog.debug(getClass(), "test_0004_createWithComplexDataModels",
                    "cid: " + cd.cid + ", name: " + cd.name + ", type: " + cd.type +
            ", notnull: " + cd.notNull + ", defaultValue: " + (cd.defaultValue != null ? cd.defaultValue.toString() : "<null>") +
            ", pkIndex: " + cd.pkIndex);
        }
    }

    @Test
    public void test_0005_addObject() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_THREE;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        UUTestDataModel model = new UUTestDataModel();
        model.name = "Foobar";
        model.team = "Boston";
        model.number = 57;
        UUTestDataModel added = db.insertObject(UUTestDataModel.class, model);
        Assert.assertNotNull(added);

        model.name = "Hello World";
        UUTestDataModel updated = db.updateObject(UUTestDataModel.class, model);
        Assert.assertNotNull(updated);
    }

    @Test
    public void test_0006_objectWithPrimitiveObjectTypes() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_FOUR;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        UUDataModelWithObjPrimitiveTypes model = new UUDataModelWithObjPrimitiveTypes();
        model.aFloat = 57.0f;
        model.anInt = 22;
        model.aPrimFloat = 1001.0f;

        UUDataModelWithObjPrimitiveTypes added = db.insertObject(UUDataModelWithObjPrimitiveTypes.class, model);
        Assert.assertNotNull(added);
        Assert.assertEquals(model.aFloat, added.aFloat);
        Assert.assertEquals(model.anInt, added.anInt);
        Assert.assertEquals(model.aPrimFloat, added.aPrimFloat, 0);

        model.aFloat = 99.0f;
        model.anInt = 1234;
        model.aPrimFloat = 129.0f;
        UUDataModelWithObjPrimitiveTypes updated = db.updateObject(UUDataModelWithObjPrimitiveTypes.class, model);
        Assert.assertNotNull(updated);
        Assert.assertEquals(model.aFloat, updated.aFloat);
        Assert.assertEquals(model.anInt, updated.anInt);
        Assert.assertEquals(model.aPrimFloat, updated.aPrimFloat, 0);
    }

    @Test
    public void test_0007_querySingleColumnValues() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_FOUR;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        UUAllColumnTypesDataModel model = UUAllColumnTypesDataModel.random();
        UUAllColumnTypesDataModel inserted = db.insertObject(UUAllColumnTypesDataModel.class, model);
        Assert.assertNotNull(inserted);
        UUAllColumnTypesDataModel.assertEquals(model, inserted);

        //db.querySingleIntCell("SELECT ")

        int intResult;
        long longResult;
        float floatResult;
        double doubleResult;
        String stringResult;
        byte[] binaryResult;

        intResult = db.querySingleIntCell("SELECT byte_object FROM all_columns", null, 0);
        Assert.assertEquals(model.byteObject.intValue(), intResult);

        intResult = db.querySingleIntCell("SELECT byte_primitive FROM all_columns", null, 0);
        Assert.assertEquals(model.bytePrimitive, intResult);

        intResult = db.querySingleIntCell("SELECT short_object FROM all_columns", null, 0);
        Assert.assertEquals(model.shortObject.intValue(), intResult);

        intResult = db.querySingleIntCell("SELECT short_primitive FROM all_columns", null, 0);
        Assert.assertEquals(model.shortPrimitive, intResult);

        intResult = db.querySingleIntCell("SELECT int_object FROM all_columns", null, 0);
        Assert.assertEquals(model.intObject.intValue(), intResult);

        intResult = db.querySingleIntCell("SELECT int_primitive FROM all_columns", null, 0);
        Assert.assertEquals(model.intPrimitive, intResult);

        longResult = db.querySingleLongCell("SELECT long_object FROM all_columns", null, 0);
        Assert.assertEquals(model.longObject.longValue(), longResult);

        longResult = db.querySingleLongCell("SELECT long_primitive FROM all_columns", null, 0);
        Assert.assertEquals(model.longPrimitive, longResult);

        floatResult = db.querySingleFloatCell("SELECT float_object FROM all_columns", null, 0);
        Assert.assertEquals(model.floatObject, floatResult, 0);

        floatResult = db.querySingleFloatCell("SELECT float_primitive FROM all_columns", null, 0);
        Assert.assertEquals(model.floatPrimitive, floatResult, 0);

        doubleResult = db.querySingleDoubleCell("SELECT double_object FROM all_columns", null, 0);
        Assert.assertEquals(model.doubleObject, doubleResult, 0);

        doubleResult = db.querySingleDoubleCell("SELECT double_primitive FROM all_columns", null, 0);
        Assert.assertEquals(model.doublePrimitive, doubleResult, 0);

        intResult = db.querySingleIntCell("SELECT character_object FROM all_columns", null, 0);
        Assert.assertEquals(model.characterObject.charValue(), (char)intResult);

        intResult = db.querySingleIntCell("SELECT character_primitive FROM all_columns", null, 0);
        Assert.assertEquals(model.characterPrimitive, (char)intResult);

        stringResult = db.querySingleStringCell("SELECT string_object FROM all_columns", null, null);
        Assert.assertEquals(model.stringObject, stringResult);

        //binaryResult = db.querySingleBlobCell("SELECT byte_array_object FROM all_columns", null, null);
        //Assert.assertArrayEquals(model.byteArrayObject, binaryResult);

        binaryResult = db.querySingleBlobCell("SELECT byte_array_primitive FROM all_columns", null, null);
        Assert.assertArrayEquals(model.byteArrayPrimitive, binaryResult);
    }

    @Test
    public void test_0008_testAutoIncrementId_PrimitiveInt() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_FOUR;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        for (int i = 1; i < 20; i++)
        {
            insertAutoIncrementPrimIntObject(db, i);
        }
    }

    @Test
    public void test_0009_testAutoIncrementId_ObjectInt() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_FOUR;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        for (int i = 1; i < 20; i++)
        {
            insertAutoIncrementObjIntObject(db, i);
        }
    }

    @Test
    public void test_0010_testDeletObjectList_BulkStyle() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_FOUR;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        int limit = 20;
        for (int i = 1; i <= limit; i++)
        {
            insertAutoIncrementPrimIntObject(db, i);
        }

        int count = db.count(UUAutoIncrementPrimIntDataModel.class, null, null);
        Assert.assertEquals(limit, count);

        ArrayList<UUAutoIncrementPrimIntDataModel> list = db.queryMultipleObjects(UUAutoIncrementPrimIntDataModel.class, null, null, null, null);
        db.deleteObjectList(UUAutoIncrementPrimIntDataModel.class, list);

        count = db.count(UUAutoIncrementPrimIntDataModel.class);
        Assert.assertEquals(0, count);
    }

    @Test
    public void test_0011_testDeletObjectList_NonBulkStyle() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_FOUR;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        int limit = 20;
        for (int i = 1; i <= limit; i++)
        {
            UUDataModelWithCompoundKey m = UUDataModelWithCompoundKey.random();
            db.insertObject(UUDataModelWithCompoundKey.class, m);
        }

        int count = db.count(UUDataModelWithCompoundKey.class, null, null);
        Assert.assertEquals(limit, count);

        ArrayList<UUDataModelWithCompoundKey> list = db.queryMultipleObjects(UUDataModelWithCompoundKey.class, null, null, null, null);
        db.deleteObjectList(UUDataModelWithCompoundKey.class, list);

        count = db.count(UUDataModelWithCompoundKey.class);
        Assert.assertEquals(0, count);
    }

    @Test
    public void test_0012_testUpdateNull() throws Exception
    {
        Context ctx = getContext();
        UUTestDatabase.DbDef.CURRENT_VERSION = UUTestDatabase.DbDef.VERSION_FOUR;
        ctx.deleteDatabase(UUTestDatabase.NAME);

        UUTestDatabase db = new UUTestDatabase(ctx);

        UUDataModelWithCompoundKey m = UUDataModelWithCompoundKey.random();
        UUDataModelWithCompoundKey inserted = db.insertObject(UUDataModelWithCompoundKey.class, m);
        UUDataModelWithCompoundKey.assertEquals(m, inserted);

        m.data = null;
        UUDataModelWithCompoundKey updated = db.updateObject(UUDataModelWithCompoundKey.class, m);
        UUDataModelWithCompoundKey.assertEquals(m, updated);
    }

    private void insertAutoIncrementPrimIntObject(@NonNull final UUDatabase db, int expectedId)
    {
        UUAutoIncrementPrimIntDataModel model = UUAutoIncrementPrimIntDataModel.random();
        model.id = 0;

        UUAutoIncrementPrimIntDataModel inserted = db.insertObject(UUAutoIncrementPrimIntDataModel.class, model);
        Assert.assertEquals(expectedId, inserted.id);
        model.id = expectedId;
        UUAutoIncrementPrimIntDataModel.assertEquals(model, inserted);

        int count = db.count(UUAutoIncrementPrimIntDataModel.class, null, null);
        Assert.assertEquals(expectedId, count);
    }

    private void insertAutoIncrementObjIntObject(@NonNull final UUDatabase db, int expectedId)
    {
        UUAutoIncrementObjIntDataModel model = UUAutoIncrementObjIntDataModel.random();
        model.id = null;

        UUAutoIncrementObjIntDataModel inserted = db.insertObject(UUAutoIncrementObjIntDataModel.class, model);
        Assert.assertNotNull(inserted.id);
        Assert.assertEquals(expectedId, (int)inserted.id);
        model.id = expectedId;
        UUAutoIncrementObjIntDataModel.assertEquals(model, inserted);

        int count = db.count(UUAutoIncrementObjIntDataModel.class, null, null);
        Assert.assertEquals(expectedId, count);
    }

}


