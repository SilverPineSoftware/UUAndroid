package uu.toolbox;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import uu.toolbox.data.UUContentValues;
import uu.toolbox.data.UUCursor;
import uu.toolbox.data.UUDataModel;
import uu.toolbox.data.UUSql;
import uu.toolbox.data.UUSqlColumn;

public class UUSqlTests
{
    @Test
    public void test_0000_buildCreateSqlNullPrimaryKey() throws Exception
    {
        String tableName = "uu_test_table";

        HashMap<Object, Object> map = new LinkedHashMap<>();
        map.put("id", UUSqlColumn.Type.INTEGER);
        map.put("name", UUSqlColumn.Type.TEXT);
        map.put("photo", UUSqlColumn.Type.BLOB);
        map.put("timestamp", UUSqlColumn.Type.REAL);

        String sql = UUSql.buildCreateSql(tableName, map, null);
        Assert.assertNotNull("Expect buildCreateSql to generate non null string", sql);

        String expected = "CREATE TABLE uu_test_table (id INTEGER, name TEXT, photo BLOB, timestamp REAL);";
        Assert.assertEquals("Expect create sql syntax to be correct", expected, sql);
    }

    @Test
    public void test_0001_buildCreateSqlWithPrimaryKey() throws Exception
    {
        String tableName = "uu_test_table";

        HashMap<Object, Object> map = new LinkedHashMap<>();
        map.put("id", UUSqlColumn.Type.INTEGER);
        map.put("name", UUSqlColumn.Type.TEXT);
        map.put("photo", UUSqlColumn.Type.BLOB);
        map.put("timestamp", UUSqlColumn.Type.REAL);

        String sql = UUSql.buildCreateSql(tableName, map, "id");
        Assert.assertNotNull("Expect buildCreateSql to generate non null string", sql);

        String expected = "CREATE TABLE uu_test_table (id INTEGER, name TEXT, photo BLOB, timestamp REAL, PRIMARY KEY(id));";
        Assert.assertEquals("Expect create sql syntax to be correct", expected, sql);
    }

    @Test
    public void test_0002_buildCreateSqlFromDataModel() throws Exception
    {
        String sql = UUSql.buildCreateSql(new FakeDataModel(), 1);
        Assert.assertNotNull("Expect buildCreateSql to generate non null string", sql);

        String expected = FakeDataModel.V1_CREATE;
        Assert.assertEquals("Expect create sql syntax to be correct for version 1", expected, sql);

        sql = UUSql.buildCreateSql(new FakeDataModel(), 2);
        Assert.assertNotNull("Expect buildCreateSql to generate non null string", sql);

        expected = FakeDataModel.V2_CREATE;
        Assert.assertEquals("Expect create sql syntax to be correct for version 2", expected, sql);
    }

    @Test
    public void test_0003_buildUpgradeSqlUpgrade_AddOneColumn() throws Exception
    {
        ArrayList<String> lines = new ArrayList<>();
        UUSql.appendUpgradeTableSql(lines, new FakeDataModel(), 1, 2);
        Assert.assertEquals("Expect number of sql lines to be correct", 1, lines.size());

        String sql = lines.get(0);
        String expected = FakeDataModel.V1_V2_UPGRADE;
        Assert.assertEquals("Expect create sql syntax to be correct for version 1 to 2 upgrade", expected, sql);
    }

    @Test
    public void test_0004_buildDowngradeSqlUpgrade_DropOneColumn() throws Exception
    {
        ArrayList<String> lines = new ArrayList<>();
        UUSql.appendUpgradeTableSql(lines, new FakeDataModel(), 2, 1);
        Assert.assertEquals("Expect number of sql lines to be correct", 4, lines.size());

        String sql = lines.get(0);
        String expected = FakeDataModel.V2_V1_DOWNGRADE_RENAME;
        Assert.assertEquals("Expect create sql syntax to be correct for version 2 to 1 downgrade rename", expected, sql);

        sql = lines.get(1);
        expected = FakeDataModel.V1_CREATE;
        Assert.assertEquals("Expect create sql syntax to be correct for version 2 to 1 downgrade create new table", expected, sql);

        sql = lines.get(2);
        expected = FakeDataModel.V2_V1_SELECT_INTO;
        Assert.assertEquals("Expect create sql syntax to be correct for version 2 to 1 downgrade insert old data", expected, sql);

        sql = lines.get(3);
        expected = FakeDataModel.DROP_V2_TEMP_TABLE;
        Assert.assertEquals("Expect create sql syntax to be correct for version 2 to 1 downgrade drop old", expected, sql);
    }

    @Test
    public void test_0005_buildInsertSelectIntoTableSql() throws Exception
    {
        String sql = UUSql.buildSelectIntoTableCopy("source_table", "dest_table", new String[] {"one, two, three"});
        Assert.assertNotNull("Expect insert select into sql to not be null", sql);

        String expected = "INSERT INTO dest_table SELECT one, two, three FROM source_table;";
        Assert.assertEquals("Expect insert select into sql to be correct", expected, sql);
    }

    @Test
    public void test_0006_analyzeModelSetsNoChange() throws Exception
    {
        ArrayList<UUDataModel> fromModels = new ArrayList<>();
        ArrayList<UUDataModel> toModels = new ArrayList<>();
        ArrayList<UUDataModel> addedModels = new ArrayList<>();
        ArrayList<UUDataModel> deletedModels = new ArrayList<>();
        ArrayList<UUDataModel> migratedModels = new ArrayList<>();

        fromModels.add(new FakeDataModel());

        toModels.add(new FakeDataModel());

        UUSql.analyzeModelSets(fromModels, toModels, addedModels, deletedModels, migratedModels);
        Assert.assertEquals("Expect no added models", 0, addedModels.size());
        Assert.assertEquals("Expect no delete models", 0, deletedModels.size());
        Assert.assertEquals("Expect one migrated models", 1, migratedModels.size());

        UUDataModel migrated = migratedModels.get(0);
        Assert.assertEquals("Expect the correct migrated model", migrated.getClass(), FakeDataModel.class);
    }

    @Test
    public void test_0007_analyzeModelSetsAddOne() throws Exception
    {
        ArrayList<UUDataModel> fromModels = new ArrayList<>();
        ArrayList<UUDataModel> toModels = new ArrayList<>();
        ArrayList<UUDataModel> addedModels = new ArrayList<>();
        ArrayList<UUDataModel> deletedModels = new ArrayList<>();
        ArrayList<UUDataModel> migratedModels = new ArrayList<>();

        fromModels.add(new FakeDataModel());

        toModels.add(new FakeDataModel());
        toModels.add(new AnotherFakeDataModel());

        UUSql.analyzeModelSets(fromModels, toModels, addedModels, deletedModels, migratedModels);
        Assert.assertEquals("Expect one added models", 1, addedModels.size());
        Assert.assertEquals("Expect no delete models", 0, deletedModels.size());
        Assert.assertEquals("Expect one migrated models", 1, migratedModels.size());

        UUDataModel migrated = migratedModels.get(0);
        Assert.assertEquals("Expect the correct migrated model", migrated.getClass(), FakeDataModel.class);

        UUDataModel added = addedModels.get(0);
        Assert.assertEquals("Expect the correct added model", added.getClass(), AnotherFakeDataModel.class);
    }

    @Test
    public void test_0008_analyzeModelSetsDeleteOne() throws Exception
    {
        ArrayList<UUDataModel> fromModels = new ArrayList<>();
        ArrayList<UUDataModel> toModels = new ArrayList<>();
        ArrayList<UUDataModel> addedModels = new ArrayList<>();
        ArrayList<UUDataModel> deletedModels = new ArrayList<>();
        ArrayList<UUDataModel> migratedModels = new ArrayList<>();

        fromModels.add(new FakeDataModel());
        fromModels.add(new AnotherFakeDataModel());

        toModels.add(new AnotherFakeDataModel());

        UUSql.analyzeModelSets(fromModels, toModels, addedModels, deletedModels, migratedModels);
        Assert.assertEquals("Expect no added models", 0, addedModels.size());
        Assert.assertEquals("Expect one delete models", 1, deletedModels.size());
        Assert.assertEquals("Expect one migrated models", 1, migratedModels.size());

        UUDataModel migrated = migratedModels.get(0);
        Assert.assertEquals("Expect the correct migrated model", migrated.getClass(), AnotherFakeDataModel.class);

        UUDataModel deleted = deletedModels.get(0);
        Assert.assertEquals("Expect the correct deleted model", deleted.getClass(), FakeDataModel.class);
    }

    @Test
    public void test_0008_analyzeModelSetsAndAndDeleteOne() throws Exception
    {
        ArrayList<UUDataModel> fromModels = new ArrayList<>();
        ArrayList<UUDataModel> toModels = new ArrayList<>();
        ArrayList<UUDataModel> addedModels = new ArrayList<>();
        ArrayList<UUDataModel> deletedModels = new ArrayList<>();
        ArrayList<UUDataModel> migratedModels = new ArrayList<>();

        fromModels.add(new FakeDataModel());
        fromModels.add(new AnotherFakeDataModel());

        toModels.add(new AnotherFakeDataModel());
        toModels.add(new ThirdFakeDataModel());

        UUSql.analyzeModelSets(fromModels, toModels, addedModels, deletedModels, migratedModels);
        Assert.assertEquals("Expect one added models", 1, addedModels.size());
        Assert.assertEquals("Expect one delete models", 1, deletedModels.size());
        Assert.assertEquals("Expect one migrated models", 1, migratedModels.size());

        UUDataModel migrated = migratedModels.get(0);
        Assert.assertEquals("Expect the correct migrated model", migrated.getClass(), AnotherFakeDataModel.class);

        UUDataModel deleted = deletedModels.get(0);
        Assert.assertEquals("Expect the correct deleted model", deleted.getClass(), FakeDataModel.class);

        UUDataModel added = addedModels.get(0);
        Assert.assertEquals("Expect the correct added model", added.getClass(), ThirdFakeDataModel.class);
    }


    private static class FakeDataModel implements UUDataModel
    {
        // Unit test expected results
        static String V1_CREATE = "CREATE TABLE fake_model (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, team TEXT);";
        static String V2_CREATE = "CREATE TABLE fake_model (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, team TEXT, number INTEGER);";
        static String V1_V2_UPGRADE = "ALTER TABLE fake_model ADD COLUMN number INTEGER;";
        static String V2_V1_DOWNGRADE_RENAME = "ALTER TABLE fake_model RENAME TO fake_model_old_2;";
        static String V2_V1_SELECT_INTO = "INSERT INTO fake_model SELECT id, name, team FROM fake_model_old_2;";
        static String DROP_V2_TEMP_TABLE = "DROP TABLE IF EXISTS fake_model_old_2;";


        private static String TABLE_NAME = "fake_model";

        private static String ID_COLUMN = "id";
        private static String NAME_COLUMN = "name";
        private static String NUMBER_COLUMN = "number";
        private static String TEAM_COLUMN = "team";

        private static int VERSION_ONE = 1;
        private static int VERSION_TWO = 2;

        private int id;
        private String name;
        private int number;
        private String team;

        @NonNull
        @Override
        public String getTableName()
        {
            return TABLE_NAME;
        }

        @NonNull
        @Override
        public HashMap<Object, Object> getColumnMap(int version)
        {
            // Use LinkedHashMap to preserve insertion order for unit test validation.

            HashMap<Object, Object> map = new LinkedHashMap<>();

            if (version >= VERSION_ONE)
            {
                map.put(ID_COLUMN, UUSqlColumn.Type.INTEGER_PRIMARY_KEY_AUTOINCREMENT);
                map.put(NAME_COLUMN, UUSqlColumn.Type.TEXT);
                map.put(TEAM_COLUMN, UUSqlColumn.Type.TEXT);
            }

            if (version >= VERSION_TWO)
            {
                map.put(NUMBER_COLUMN, UUSqlColumn.Type.INTEGER);
            }

            return map;
        }

        @Nullable
        @Override
        public String getPrimaryKeyColumnName()
        {
            // Return NULL because create uses auto increment column
            return null;
        }

        @NonNull
        @Override
        public String getPrimaryKeyWhereClause()
        {
            return UUSql.buildSingleColumnWhere(ID_COLUMN);
        }

        @NonNull
        @Override
        public String[] getPrimaryKeyWhereArgs()
        {
            return new String[]{String.valueOf(id)};
        }

        @NonNull
        @Override
        public ContentValues getContentValues(int version)
        {
            ContentValues cv = new ContentValues();

            if (version >= VERSION_ONE)
            {
                UUContentValues.putInteger(cv, ID_COLUMN, id);
                UUContentValues.putString(cv, NAME_COLUMN, name);
                UUContentValues.putString(cv, TEAM_COLUMN, team);
            }

            if (version >= VERSION_TWO)
            {
                UUContentValues.putInteger(cv, NUMBER_COLUMN, number);
            }

            return cv;
        }

        @Override
        public void fillFromCursor(@NonNull Cursor cursor)
        {
            id = UUCursor.safeGetInt(cursor, ID_COLUMN);
            name = UUCursor.safeGetString(cursor, NAME_COLUMN);
            team = UUCursor.safeGetString(cursor, TEAM_COLUMN);
            number = UUCursor.safeGetInt(cursor, NUMBER_COLUMN);
        }
    }

    private static class AnotherFakeDataModel implements UUDataModel
    {
        @NonNull
        @Override
        public String getTableName()
        {
            return "unused";
        }

        @NonNull
        @Override
        public HashMap<Object, Object> getColumnMap(int version)
        {
            return new HashMap<>();
        }

        @Nullable
        @Override
        public String getPrimaryKeyColumnName()
        {
            return null;
        }

        @NonNull
        @Override
        public String getPrimaryKeyWhereClause()
        {
            return "";
        }

        @NonNull
        @Override
        public String[] getPrimaryKeyWhereArgs()
        {
            return new String[] { };
        }

        @NonNull
        @Override
        public ContentValues getContentValues(int version)
        {
            return new ContentValues();
        }

        @Override
        public void fillFromCursor(@NonNull Cursor cursor)
        {
        }
    }

    private static class ThirdFakeDataModel implements UUDataModel
    {
        @NonNull
        @Override
        public String getTableName()
        {
            return "unused";
        }

        @NonNull
        @Override
        public HashMap<Object, Object> getColumnMap(int version)
        {
            return new HashMap<>();
        }

        @Nullable
        @Override
        public String getPrimaryKeyColumnName()
        {
            return null;
        }

        @NonNull
        @Override
        public String getPrimaryKeyWhereClause()
        {
            return "";
        }

        @NonNull
        @Override
        public String[] getPrimaryKeyWhereArgs()
        {
            return new String[] { };
        }

        @NonNull
        @Override
        public ContentValues getContentValues(int version)
        {
            return new ContentValues();
        }

        @Override
        public void fillFromCursor(@NonNull Cursor cursor)
        {
        }
    }
}
