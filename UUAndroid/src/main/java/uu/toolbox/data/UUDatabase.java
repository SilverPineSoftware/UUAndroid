package uu.toolbox.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import uu.toolbox.core.UUCloseable;
import uu.toolbox.core.UUListDelegate;
import uu.toolbox.core.UUObjectDelegate;
import uu.toolbox.core.UUString;
import uu.toolbox.core.UUThread;
import uu.toolbox.logging.UULog;

/**
 * UUDatabase
 * 
 * Useful Utilities - Extension methods for SQLiteDatabase
 *  
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class UUDatabase
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Variables
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    private final Context applicationContext;
    private UUSQLiteDatabase database;
    private final UUDatabaseDefinition databaseDefinition;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Construction
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    protected UUDatabase(@NonNull final Context context, @NonNull final UUDatabaseDefinition databaseDefinition)
    {
        applicationContext = context.getApplicationContext();
        this.databaseDefinition = databaseDefinition;
    }

    public Context getApplicationContext()
    {
        return applicationContext;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Database Lifecyle
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void closeDatabase()
    {
        safeClose(database);
        database = null;
    }

    public void deleteDatabase()
    {
        closeDatabase();
        safeDeleteDatabase();
    }

    public UUSQLiteDatabase getReadOnlyDatabase()
    {
        if (database == null)
        {
            database = openDatabase();
        }

        return database;
    }

    public UUSQLiteDatabase getReadWriteDatabase()
    {
        if (database == null)
        {
            database = openDatabase();
        }

        return database;
    }

    @NonNull
    public UUDatabaseDefinition getDatabaseDefinition()
    {
        return databaseDefinition;
    }

    @NonNull
    public String getDatabaseName()
    {
        return databaseDefinition.getDatabaseName();
    }

    public int getDatabaseVersion()
    {
        return databaseDefinition.getVersion();
    }

    @NonNull
    protected abstract UUSQLiteDatabase openDatabase();

    protected void handleCreate(@NonNull UUSQLiteDatabase db)
    {
        try
        {
            int version = databaseDefinition.getVersion();

            ArrayList<String> lines = new ArrayList<>();
            UUSql.appendCreateLines(lines, databaseDefinition, version);

            db.beginTransaction();

            for (String line : lines)
            {
                UULog.debug(getClass(), "handleCreate", line);
                db.execSQL(line, null);
            }

            handlePostCreate(db, version);

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("handleCreate", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    protected void handleOpen(@NonNull UUSQLiteDatabase db)
    {
        try
        {
            handlePostOpen(db, db.getVersion());
        }
        catch (Exception ex)
        {
            logException("handleOpen", ex);
        }
    }

    protected void handleMigrate(@NonNull final UUSQLiteDatabase db, final int oldVersion, final int newVersion)
    {
        try
        {
            ArrayList<String> lines = new ArrayList<>();
            UUSql.appendUpgradeLines(lines, databaseDefinition, oldVersion, newVersion);

            db.beginTransaction();

            for (String line : lines)
            {
                UULog.debug(getClass(), "handleMigrate", line);
                db.execSQL(line, null);
            }

            handlePostUpgrade(db, oldVersion, newVersion);

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("handleMigrate", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    protected void handlePostOpen(@NonNull UUSQLiteDatabase db, int version)
    {

    }

    protected void handlePostCreate(@NonNull UUSQLiteDatabase db, int version)
    {

    }

    protected void handlePostUpgrade(@NonNull UUSQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

	////////////////////////////////////////////////////////////////////////////////////////////////
    // UUDataModel Methods
	////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Queries the database for a set of objects
     *
     * @param type row object type
     * @param selection where clause
     * @param selectionArgs bound where arguments
     * @param orderBy order by clause
     * @param limit limit clause
     * @return a List of 
     */
    @NonNull
    public synchronized <T extends UUDataModel> ArrayList<T> queryMultipleObjects(
        @NonNull final Class<T> type,
        @Nullable final String selection,
        @Nullable String[] selectionArgs,
        @Nullable String orderBy,
        @Nullable String limit)
    {
    	ArrayList<T> results = new ArrayList<>();

		Cursor c = null;
		
    	try
    	{
            UUSQLiteDatabase db = getReadOnlyDatabase();

		    T dataModel = type.newInstance();

		    c = db.query(dataModel.getTableName(), UUSql.getColumnNames(dataModel, db.getVersion()), selection, selectionArgs, null, null, orderBy, limit);
		    
		    while (c.moveToNext())
		    {
		    	T obj = type.newInstance();
		    	obj.fillFromCursor(c);
		    	results.add(obj);
		    }
    	}
    	catch (Exception ex)
    	{
    		logException("queryMultipleObjects", ex);
    	}
    	finally
    	{
            UUCloseable.safeClose(c);
    	}
    	
    	return results;
    }

    /**
     * Queries the database for a set of objects.  Query is performed on a background thread
     * and results are delivered asynchronously to the caller
     *
     * @param type data model type
     * @param selection SQL Where clause
     * @param selectionArgs Bound arguments for SQL Where clause
     * @param orderBy SQL order by clause
     * @param limit SQL limit clause
     * @param delegate return delegate
     * @param <T> type of data model class
     */
    public <T extends UUDataModel> void queryMultipleObjects(
            @NonNull final Class<T> type,
            @Nullable final String selection,
            @Nullable final String[] selectionArgs,
            @Nullable final String orderBy,
            @Nullable final String limit,
            @NonNull final UUListDelegate<T> delegate)
    {
        UUThread.runOnBackgroundThread(() ->
        {
            ArrayList<T> results = queryMultipleObjects(type, selection, selectionArgs, orderBy, limit);
            UUListDelegate.safeInvoke(delegate, results);
        });
    }
    
    /**
     * Queries the database for a set of objects
     *
     * @param type row object type
     * @param rawSqlQuery The sql query
     * @param selectionArgs bound where arguments
     * @return a List of objects of type T
     */
    @NonNull
    public synchronized <T extends UUDataModel> ArrayList<T> rawQueryMultipleObjects(
        @NonNull final Class<T> type,
        @NonNull final String rawSqlQuery,
        @Nullable final String[] selectionArgs)
    {
    	ArrayList<T> results = new ArrayList<>();

		Cursor c = null;
		
    	try
    	{
            UUSQLiteDatabase db = getReadOnlyDatabase();

            logSql(rawSqlQuery);
		    c = db.rawQuery(rawSqlQuery, selectionArgs);
		    
		    while (c.moveToNext())
		    {
		    	T obj = type.newInstance();
		    	obj.fillFromCursor(c);
		    	results.add(obj);
		    }
    	}
    	catch (Exception ex)
    	{
    		logException("rawQueryMultipleObjects", ex);
    	}
    	finally
    	{
            UUCloseable.safeClose(c);
    	}
    	
    	return results;
    }

    /**
     * Queries the database for a set of objects
     *
     * @param type row object type
     * @param rawSqlQuery The sql query
     * @param selectionArgs bound where arguments
     * @param delegate async return delegate
     */
    public synchronized <T extends UUDataModel> void rawQueryMultipleObjects(
        @NonNull final Class<T> type,
        @NonNull final String rawSqlQuery,
        @Nullable final String[] selectionArgs,
        @NonNull final UUListDelegate<T> delegate)
    {
        UUThread.runOnBackgroundThread(() ->
        {
            ArrayList<T> results = rawQueryMultipleObjects(type, rawSqlQuery, selectionArgs);
            UUListDelegate.safeInvoke(delegate, results);
        });
    }
    
    /**
     * Queries the database for a single object
     *
     * @param type row object type
     * @param selection where clause
     * @param selectionArgs bound where arguments
     * @param orderBy order by clause
     * @return a List of 
     */
    @Nullable
    public synchronized <T extends UUDataModel> T querySingleObject(
        @NonNull final Class<T> type,
        @Nullable final String selection,
        @Nullable final String[] selectionArgs,
        @Nullable final String orderBy)
    {
    	ArrayList<T> list = queryMultipleObjects(type, selection, selectionArgs, orderBy, "1");
    	if (list.size() == 1)
    	{
    		return list.get(0);
    	}

    	return null;
    }

    /**
     * Queries the database for a single object
     *
     * @param type row object type
     * @param selection where clause
     * @param selectionArgs bound where arguments
     * @param orderBy order by clause
     * @param delegate async return delegate
     */
    public synchronized <T extends UUDataModel> void querySingleObject(
        @NonNull final Class<T> type,
        @Nullable final String selection,
        @Nullable final String[] selectionArgs,
        @Nullable final String orderBy,
        @NonNull final UUObjectDelegate<T> delegate)
    {
        UUThread.runOnBackgroundThread(() ->
        {
            T result = querySingleObject(type, selection, selectionArgs, orderBy);
            UUObjectDelegate.safeInvoke(delegate, result);
        });
    }

    /**
     * Inserts an object and then queries it
     *
     * @param type row object type
     * @param object the object to update
     * @return an object of type T
     */
    public synchronized <T extends UUDataModel> T insertObject(
        @NonNull final Class<T> type,
        @NonNull T object)
    {
        UUSQLiteDatabase db = getReadOnlyDatabase();
        ContentValues cv = object.getContentValues(db.getVersion());
    	long rowid = insert(object.getTableName(), cv);
    	return querySingleObject(type, "ROWID = ?", new String[] { String.valueOf(rowid) }, null);
    }

    /**
     * Inserts or Updates an object based on the primary key
     *
     * @param type row object type
     * @param object the object to update
     * @return an object of type T
     */
    public synchronized <T extends UUDataModel> T updateObject(
        @NonNull final Class<T> type,
        @NonNull T object)
    {
    	String whereClause = object.getPrimaryKeyWhereClause();
    	String[] whereArgs = object.getPrimaryKeyWhereArgs();
        return updateObject(type, object, whereClause, whereArgs);
    }

    /**
     * Inserts or Updates an object with a non primary key
     *
     * @param type row object type
     * @param object the object to update
     * @param columnName the column to use for the where clause
     * @param argument the single argument to bind for the where clause
     * @return an object of type T
     */
    public synchronized <T extends UUDataModel> T updateObjectWithParam(
            @NonNull final Class<T> type,
            @NonNull T object,
            @NonNull Object columnName,
            @NonNull Object argument)
    {
        String whereClause = String.format(Locale.US, "%s = ?", columnName);
        String[] whereArgs = { argument.toString() };
        return updateObject(type, object, whereClause, whereArgs);
    }

    /**
     * Inserts or Updates an object with an arbitrary where clause.
     *
     * @param type row object type
     * @param object the object to update
     * @param whereArgs the where clause to use for the update
     * @param whereClause the bound where arguments to use for the update
     * @return an object of type T
     */
    public synchronized <T extends UUDataModel> T updateObject(
            @NonNull final Class<T> type,
            @NonNull T object,
            @NonNull String whereClause,
            @NonNull String[] whereArgs)
    {
        int count = count(type, whereClause, whereArgs);
        if (count == 0)
        {
            return insertObject(type, object);
        }
        else
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();
            update(object.getTableName(), object.getContentValues(db.getVersion()), whereClause, whereArgs);
            return querySingleObject(type, whereClause, whereArgs, null);
        }
    }

    /**
     * Deletes an object
     *
     * @param type row object type
     * @param object the object to update
     * @param <T> a UUDataModel subclass
     *
     */
    public synchronized <T extends UUDataModel> void deleteObject(
            @NonNull final Class<T> type,
            @NonNull T object)
    {
        String whereClause = object.getPrimaryKeyWhereClause();
        String[] whereArgs = object.getPrimaryKeyWhereArgs();
        delete(object.getTableName(), whereClause, whereArgs);
    }


    /**
     * Delete's a list of object by primary key
     *
     * @param type row object type
     * @param objectList list of objects to delete
     * @param <T> a UUDataModel subject
     */
    public synchronized <T extends UUDataModel> void deleteObjectList(
            @NonNull final Class<T> type,
            @NonNull final ArrayList<T> objectList)
    {
        if (canBulkDelete(type))
        {
            bulkDeleteObjects(type, objectList);
        }
        else
        {
            deleteObjectsOneByOne(type, objectList);
        }
    }

    /**
     * An object can be bulk deleted if it has a single column primary key
     *
     * @param type row object type
     * @param <T> a UUDataModel subclass
     * @return
     */
    private  <T extends UUDataModel> boolean canBulkDelete(@NonNull final Class<T> type)
    {
        try
        {
            T refObj = type.newInstance();
            String[] args = refObj.getPrimaryKeyWhereArgs();
            return (args.length == 1);
        }
        catch (Exception ex)
        {
            logException("canBulkDelete", ex);
            return false;
        }
    }

    /**
     * Delete's a list of objects by primary key
     *
     * @param type row object type
     * @param objectList list of objects to delete
     * @param <T> a UUDataModel subclass
     */
    private synchronized <T extends UUDataModel> void bulkDeleteObjects(
            @NonNull final Class<T> type,
            @NonNull final ArrayList<T> objectList)
    {
        int limit = 100;

        while (objectList.size() > 0)
        {
            ArrayList<T> tmp = new ArrayList<>();
            for (int i = 0; i < limit && objectList.size() > 0; i++)
            {
                T obj = objectList.remove(0);
                tmp.add(obj);
            }

            if (tmp.size() > 0)
            {
                bulkDeleteObjectBatch(type, tmp);
            }
        }
    }

    /**
     * Delete's a list of objects by primary key with a single SQL statement
     *
     * @param type row object type
     * @param objectList list of objects to delete
     * @param <T> a UUDataModel subclass
     */
    private synchronized <T extends UUDataModel> void bulkDeleteObjectBatch(
            @NonNull final Class<T> type,
            @NonNull final ArrayList<T> objectList)
    {
        try
        {
            T refObj = type.newInstance();
            StringBuilder where = new StringBuilder();
            where.append(String.format(Locale.US, "%s IN (", refObj.getSinglePrimaryKeyColumnName()));

            String[] whereArgs = new String[objectList.size()];
            for (int i = 0; i < whereArgs.length; i++)
            {
                String[] primaryKeyWhereArgs = objectList.get(i).getPrimaryKeyWhereArgs();
                if (primaryKeyWhereArgs.length == 1)
                {
                    where.append("?");
                    whereArgs[i] = primaryKeyWhereArgs[0];
                }

                if (i < (whereArgs.length - 1))
                {
                    where.append(",");
                }
            }

            where.append(")");

            delete(refObj.getTableName(), where.toString(), whereArgs);
        }
        catch (Exception ex)
        {
            logException("doDeleteObjectList", ex);
        }
    }

    /**
     * Delete's a list of objects by primary key one bye one.  This is a slower way to delete
     *
     * @param type row object type
     * @param objectList list of objects to delete
     * @param <T> a UUDataModel subclass
     */
    private synchronized <T extends UUDataModel> void deleteObjectsOneByOne(
            @NonNull final Class<T> type,
            @NonNull final ArrayList<T> objectList)
    {
        UUSQLiteDatabase db = null;

        try
        {
            db = getReadWriteDatabase();

            db.beginTransaction();

            String tableName = tableNameForModel(type);

            for (UUDataModel obj : objectList)
            {
                db.delete(tableName, obj.getPrimaryKeyWhereClause(), obj.getPrimaryKeyWhereArgs());
            }

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("deleteObjects", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    /**
     * Inserts a list of records
     *
     * @param type model type to insert
     * @param list records to insert
     * @param <T> model type
     */
    public synchronized <T extends UUDataModel> void bulkInsert(
            @NonNull final Class<T> type,
            @NonNull final ArrayList<T> list)
    {
        bulkInsert(type, tableNameForModel(type), list);
    }

    /**
     * Inserts a list of records
     *
     * @param type model type to insert
     * @param tableName the table to insert to
     * @param list records to insert
     * @param <T> model type
     */
    public synchronized <T extends UUDataModel> void bulkInsert(
        @NonNull final Class<T> type,
        @NonNull final String tableName,
        @NonNull final ArrayList<T> list)
    {
        UUSQLiteDatabase db = null;

        try
        {
            db = getReadWriteDatabase();

            db.beginTransaction();

            int dbVersion = db.getVersion();

            for (T row : list)
            {
                db.insert(tableName, null, row.getContentValues(dbVersion));
            }

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("bulkInsert", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    /**
     * Truncates a table and replaces it with the contents of the list passed in
     *
     * @param type the data model type.
     * @param list the list of new data rows to insert.
     * @param <T> the data model type, a class that implements the UUDataModel interface.
     */
    public synchronized <T extends UUDataModel> void bulkReplace(
        @NonNull final Class<T> type,
        @NonNull ArrayList<T> list)
    {
        bulkReplace(type, list, true);
    }

    /**
     * Truncates a table and replaces it with the contents of the list passed in
     *
     * @param type the data model type.
     * @param list the list of new data rows to insert.
     * @param <T> the data model type, a class that implements the UUDataModel interface.
     * @param useTransaction flag indicating whether a transaction should be used or not.
     */
    public synchronized <T extends UUDataModel> void bulkReplace(
            @NonNull final Class<T> type,
            @NonNull ArrayList<T> list,
            final boolean useTransaction)
    {
        bulkReplace(type, tableNameForModel(type), list, useTransaction);
    }

    /**
     * Truncates a table and replaces it with the contents of the list passed in
     *
     * @param type the data model type.
     * @param tableName the table to replace records in
     * @param list the list of new data rows to insert.
     * @param <T> the data model type, a class that implements the UUDataModel interface.
     */
    public synchronized <T extends UUDataModel> void bulkReplace(
        @NonNull final Class<T> type,
        @NonNull final String tableName,
        @NonNull ArrayList<T> list)
    {
        bulkReplace(type, tableName, list, true);
    }

    /**
     * Truncates a table and replaces it with the contents of the list passed in
     *
     * @param type the data model type.
     * @param tableName the table to replace records in
     * @param list the list of new data rows to insert.
     * @param <T> the data model type, a class that implements the UUDataModel interface.
     * @param useTransaction flag indicating whether a transaction should be used or not.
     */
    public synchronized <T extends UUDataModel> void bulkReplace(
        @NonNull final Class<T> type,
        @NonNull final String tableName,
        @NonNull ArrayList<T> list,
        final boolean useTransaction)
    {
        UUSQLiteDatabase db = null;

        try
        {
            db = getReadWriteDatabase();

            if (useTransaction)
            {
                db.beginTransaction();
            }

            int dbVersion = db.getVersion();

            db.delete(tableName, null, null);

            for (T row : list)
            {
                ContentValues cv = row.getContentValues(dbVersion);
                //logContentValues("insert", cv);
                db.insert(tableName, null, cv);
            }

            if (useTransaction)
            {
                db.setTransactionSuccessful();
            }
        }
        catch (Exception ex)
        {
            logException("bulkReplace", ex);
        }
        finally
        {
            if (useTransaction)
            {
                safeEndTransaction(db);
            }
        }
    }

    /**
     * Delete's all records from a table based on the model class table name
     *
     */
    public <T extends UUDataModel> void truncateTable(@NonNull final Class<T> type)
    {
        truncateTable(tableNameForModel(type));
    }

    /**
     * Drops a table from the database
     *
     * @param type table model type
     *
     * @param <T> Generic type
     */
    public <T extends UUDataModel> void dropTable(@NonNull final Class<T> type)
    {
        dropTable(tableNameForModel(type));
    }

    /**
     * Drops a table from the database
     *
     * @param tableName the table name to drop
     *
     */
    public void dropTable(@NonNull final String tableName)
    {
        execSql(UUSql.buildDropTableSql(tableName));
    }

    /**
     * Count of all records in a table matching the specified where clause.  If
     * the where clause is null then the query will count all rows in the table
     *
     * @param type modelClass to derive table Nnme from
     * @param where SQL WHERE clause
     * @param whereArgs bound where arguments
     * @return an integer count of records matching the query
     */
    public <T extends UUDataModel> int count(
            @NonNull final Class<T> type,
            @Nullable final String where,
            @Nullable final String[] whereArgs)
    {
        return count(tableNameForModel(type), where, whereArgs);
    }

    /**
     * Count of all records in a table.
     *
     * @param type modelClass to derive table Nnme from
     * @return an integer count of records matching the query
     */
    public <T extends UUDataModel> int count(@NonNull final Class<T> type)
    {
        return count(type, null, null);
    }

    /**
     *
     * Logs all records in a table
     *
     * @param type model type to derive the table name from
     * @param message an optional message to be logged
     * @param <T> a class that implements UUDataModel
     */
    public <T extends UUDataModel> void logTable(@NonNull final Class<T> type, @Nullable final String message)
    {
        logTable(tableNameForModel(type), message);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // ExecSql Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Executes some raw SQL
     *
     * @param sql the sql statement
     */
    public void execSql(@NonNull final String sql)
    {
        execSql(sql, null);
    }

    /**
     * Executes some raw SQL
     *
     * @param sql the sql statement
     * @param bindArgs the sql bind arguments
     */
    public synchronized void execSql(
            @NonNull final String sql,
            @Nullable final Object[] bindArgs)
    {
        try
        {
            UUSQLiteDatabase db = getReadWriteDatabase();
            logSql(sql);
            db.execSQL(sql, bindArgs);
        }
        catch (Exception ex)
        {
            logException("execSql", ex);
        }
    }

    /**
     * Executes some raw SQL lines
     *
     * @param lines the sql statements
     */
    public synchronized void execSqlLines(
            @NonNull final ArrayList<String> lines)
    {
        UUSQLiteDatabase db = null;

        try
        {
            db = getReadWriteDatabase();

            db.beginTransaction();

            for (String line : lines)
            {
                execSql(line);
            }

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("execSqlLines", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    /**
     * Executes some raw SQL lines with bind arguments
     *
     * @param lines the sql statements
     */
    public synchronized void execSqlLinesWithArgs(
        @NonNull final ArrayList<Pair<String, Object[]>> lines)
    {
        UUSQLiteDatabase db = null;

        try
        {
            db = getReadWriteDatabase();

            db.beginTransaction();

            for (Pair<String, Object[]> args : lines)
            {
                execSql(args.first, args.second);
            }

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("execSqlLinesWithArgs", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // SQL Modify Methods (Insert, Update, Replace)
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Inserts a new table row
     *
     * @param tableName name of the table
     * @param cv list of columns to update
     *
     * @return the row id that was updated
     */
    public synchronized long insert(
            @NonNull final String tableName,
            @NonNull final ContentValues cv)
    {
        long rowid;

        try
        {
            UUSQLiteDatabase db = getReadWriteDatabase();

            logContentValues("insert", cv);
            rowid = db.insert(tableName, null, cv);

            if (rowid == -1)
            {
                UULog.warn(UUDatabase.class, "insert", "DB insert returned -1, this indicates an error!");
            }
        }
        catch (Exception ex)
        {
            logException("insert", ex);
            rowid = -1;
        }

        return rowid;
    }

    /**
     * Inserts or Updates a table row
     *
     * @param tableName name of the table
     * @param cv list of columns to update
     *
     * @return the row id that was updated
     */
    public synchronized long replace(
            @NonNull final String tableName,
            @NonNull final ContentValues cv)
    {
        long rowid;

        try
        {
            UUSQLiteDatabase db = getReadWriteDatabase();

            logContentValues("replace", cv);
            rowid = db.replace(tableName, null, cv);

            if (rowid == -1)
            {
                UULog.warn(UUDatabase.class, "replace", "DB replace returned -1, this indicates an error!");
            }
        }
        catch (Exception ex)
        {
            logException("replace", ex);
            rowid = -1;
        }

        return rowid;
    }

    /**
     * Updates a table row
     *
     * @param tableName name of the table
     * @param cv list of columns to update
     * @param whereClause the where clause
     * @param whereArgs args
     *
     */
    public synchronized void update(
            @NonNull final String tableName,
            @NonNull final ContentValues cv,
            @Nullable final String whereClause,
            @Nullable final String[] whereArgs)
    {
        try
        {
            UUSQLiteDatabase db = getReadWriteDatabase();

            logContentValues("update", cv);
            db.update(tableName, cv, whereClause, whereArgs);
        }
        catch (Exception ex)
        {
            logException("update", ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Query Multiple Row Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Query Single Row Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Query Single Cell Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Runs a query expecting a single cell as the result
     *
     * @param resultClass The class of the expected result.  Only valid values are String, byte[]
     *                    or primitive's
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
   /* @Nullable
    public synchronized <T> T querySingleCell(
            @NonNull Class<T> resultClass,
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            @Nullable final T defaultValue)
    {
        Cursor c = null;
        T result = null;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, selectionArgs);

            if (c.moveToFirst())
            {
                Object objectResult = UUCursor.safeGet(resultClass, c, 0, defaultValue);
                result = UUObject.safeCast(resultClass, objectResult);
            }
        }
        catch (Exception ex)
        {
            logException("querySingleCell", ex);
            result = null;
        }
        finally
        {
            UUCloseable.safeClose(c);
        }

        if (result == null)
        {
            result = defaultValue;
        }

        return result;
    }*/

    /**
     * Runs a query expecting a single integer cell as the result
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    @Nullable
    public synchronized String querySingleStringCell(
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            @Nullable final String defaultValue)
    {
        //return querySingleCell(String.class, sql, selectionArgs, defaultValue);

        Cursor c = null;
        String result = defaultValue;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, selectionArgs);

            if (c.moveToFirst())
            {
                result = c.getString(0);
            }
        }
        catch (Exception ex)
        {
            logException("querySingleStringCell", ex);
            result = defaultValue;
        }
        finally
        {
            UUCloseable.safeClose(c);
        }

        return result;
    }

    /**
     * Runs a query expecting a single integer cell as the result
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    public synchronized int querySingleIntCell(
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            final int defaultValue)
    {
        //Integer result = querySingleCell(Integer.class, sql, selectionArgs, null);
        //return (result != null ? result : defaultValue);

        Cursor c = null;
        int result = defaultValue;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, selectionArgs);

            if (c.moveToFirst())
            {
                result = c.getInt(0);
            }
        }
        catch (Exception ex)
        {
            logException("querySingleIntCell", ex);
            result = defaultValue;
        }
        finally
        {
            UUCloseable.safeClose(c);
        }

        return result;
    }

    /**
     * Runs a query expecting a single long cell as the result
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    public synchronized long querySingleLongCell(
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            final long defaultValue)
    {
        //Long result = querySingleCell(Long.class, sql, selectionArgs, null);
        //return (result != null ? result : defaultValue);

        Cursor c = null;
        long result = defaultValue;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, selectionArgs);

            if (c.moveToFirst())
            {
                result = c.getLong(0);
            }
        }
        catch (Exception ex)
        {
            logException("querySingleLongCell", ex);
            result = defaultValue;
        }
        finally
        {
            UUCloseable.safeClose(c);
        }

        return result;
    }

    /**
     * Runs a query expecting a single float cell as the result
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    public synchronized float querySingleFloatCell(
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            final float defaultValue)
    {
//        Float result = querySingleCell(Float.class, sql, selectionArgs, null);
//        return (result != null ? result : defaultValue);

        Cursor c = null;
        float result = defaultValue;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, selectionArgs);

            if (c.moveToFirst())
            {
                result = c.getFloat(0);
            }
        }
        catch (Exception ex)
        {
            logException("querySingleFloatCell", ex);
            result = defaultValue;
        }
        finally
        {
            UUCloseable.safeClose(c);
        }

        return result;
    }

    /**
     * Runs a query expecting a single double cell as the result
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    public synchronized double querySingleDoubleCell(
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            final double defaultValue)
    {
        //        Float result = querySingleCell(Double.class, sql, selectionArgs, null);
        //        return (result != null ? result : defaultValue);

        Cursor c = null;
        double result = defaultValue;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, selectionArgs);

            if (c.moveToFirst())
            {
                result = c.getDouble(0);
            }
        }
        catch (Exception ex)
        {
            logException("querySingleDoubleCell", ex);
            result = defaultValue;
        }
        finally
        {
            UUCloseable.safeClose(c);
        }

        return result;
    }

    /**
     * Runs a query expecting a single float cell as the result
     *
     * @param sql the sql to run
     * @param selectionArgs bound where arguments
     * @param defaultValue the default value to return if the result is null
     * @return a result
     */
    @Nullable
    public synchronized byte[] querySingleBlobCell(
            @NonNull final String sql,
            @Nullable final String[] selectionArgs,
            @Nullable final byte[] defaultValue)
    {
        //return querySingleCell(byte[].class, sql, selectionArgs, null);

        Cursor c = null;
        byte[] result = defaultValue;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, selectionArgs);

            if (c.moveToFirst())
            {
                result = c.getBlob(0);
            }
        }
        catch (Exception ex)
        {
            logException("querySingleBlobCell", ex);
            result = defaultValue;
        }
        finally
        {
            UUCloseable.safeClose(c);
        }

        return result;
    }

    /**
     * Count of all records in a table matching the specified where clause.  If
     * the where clause is null then the query will count all rows in the table
     *
     * @param tableName tableName
     * @param where SQL WHERE clause
     * @param whereArgs bound where arguments
     * @return an integer count of records matching the query
     */
    public int count(
            @NonNull final String tableName,
            @Nullable final String where,
            @Nullable final String[] whereArgs)
    {
        String sql = UUSql.buildCountSql(tableName, where);
        return querySingleIntCell(sql, whereArgs, 0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Query Single Column Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Runs a query expecting a single column of strings
     *
     * @param sql the sql to run
     * @return a result
     */
    @NonNull
    public synchronized ArrayList<String> listSingleStringColumn(
            @NonNull final String sql,
            @Nullable final String[] args)
    {
        Cursor c = null;
        ArrayList<String> results = new ArrayList<>();

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            logSql(sql);
            c = db.rawQuery(sql, args);

            while (c.moveToNext())
            {
                String val = c.getString(0);
                results.add(val);
            }
        }
        catch (Exception ex)
        {
            logException("listSingleStringColumn", ex);
        }
        finally
        {
            UUCloseable.safeClose(c);
        }

        return results;
    }

    /**
     * Lists all table names in a SQLiteDatabase
     *
     * @return a list of table names
     */
    @NonNull
    public ArrayList<String> listTableNames()
    {
        String sql = "SELECT name FROM sqlite_master WHERE type='table';";
        return listSingleStringColumn(sql, null);
    }

    /**
     * checks to see if a table exists
     *
     * @return true if the table exists, false otherwise
     */
    public boolean doesTableExist(@NonNull final String tableName)
    {
        int count = count("sqlite_master", "type='table' AND name = ?", new String[] { tableName });
        return (count > 0);
    }

    /**
     * Queries the database for the column names and types
     *
     * @param tableName the table name to query
     * @return a dictionary of column to column type
     */
    @NonNull
    public HashMap<Object,Object> getTableColumnMap(
        @NonNull final String tableName)
    {
        HashMap<Object, Object> map = new HashMap<>();

        Cursor c = null;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();
            c = db.rawQuery("PRAGMA table_info(?)", new String[] { tableName });

            while (c.moveToNext())
            {
                String name = UUCursor.safeGetString(c, "name");
                String type = UUCursor.safeGetString(c, "type");
                map.put(name, type);
            }
        }
        catch (Exception ex)
        {
            logException("getTableColumnMap", ex);
        }
        finally
        {
            UUCloseable.safeClose(c);
        }

        return map;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Deletes a table row using a transaction
     *
     * @param tableName name of the table
     * @param whereClause WHERE clause to use when deleting
     * @param whereArgs bound arguments applied to ?'s in the whereClause
     */
    public synchronized void delete(
            @NonNull final String tableName,
            @Nullable final String whereClause,
            @Nullable final String[] whereArgs)
    {
        delete(tableName, whereClause, whereArgs, true);
    }

    /**
     * Deletes a table row optionally using a transaction
     *
     * @param tableName name of the table
     * @param whereClause WHERE clause to use when deleting
     * @param whereArgs bound arguments applied to ?'s in the whereClause
     */
    public synchronized void delete(
        @NonNull final String tableName,
        @Nullable final String whereClause,
        @Nullable final String[] whereArgs,
        final boolean useTransaction)
    {
        UUSQLiteDatabase db = null;

        try
        {
            db = getReadWriteDatabase();

            if (useTransaction)
            {
                db.beginTransaction();
            }

            logSql("DELETE FROM " + tableName + " " + whereClause + ", Args: " + UUString.componentsJoinedByString(whereArgs, ","));
            db.delete(tableName, whereClause, whereArgs);

            if (useTransaction)
            {
                db.setTransactionSuccessful();
            }
        }
        catch (Exception ex)
        {
            logException("delete", ex);
        }
        finally
        {
            if (useTransaction)
            {
                safeEndTransaction(db);
            }
        }
    }

    /**
     *
     * Delete's all records from a table
     *
     * @param tableName the table to truncate
     *
     */
    public void truncateTable(@NonNull final String tableName)
    {
        delete(tableName, null, null);
    }

    /**
     * WARNING - This method will delete all rows from all tables
     *
     */
    public synchronized void resetDatabase()
    {
        try
        {
            ArrayList<String> tables = listTableNames();
            for (String table : tables)
            {
                // This is a special android table that we will leave alone.
                if ("android_metadata".equalsIgnoreCase(table))
                {
                    continue;
                }

                truncateTable(table);
            }
        }
        catch (Exception ex)
        {
            logException("resetDatabase", ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Table Logging Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     *
     * Logs all records in a table
     *
      * @param tableName table name to query
     *  @param message an optional message
     */
    public void logTable(@NonNull final String tableName, @Nullable final String message)
    {
        logTable(tableName, null, null, null, message);
    }

    /**
     *
     * Logs all records matching the given query
     *
     * @param tableName table name to query
     * @param columns the columns to select
     * @param where the SQL Where clause to use
     * @param whereArgs SQL Where bound arguments
     * @param message an optional message
     */
    public synchronized void logTable(
        @NonNull final String tableName,
        @Nullable String[] columns,
        @Nullable final String where,
        @Nullable final String[] whereArgs,
        @Nullable final String message)
    {
        Cursor c = null;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            if (columns == null)
            {
                columns = new String[] { UUSql.ROWID_COLUMN, "*" };
            }

            c = db.query(tableName, columns, where, whereArgs, null, null, null, null);

            UULog.debug(UUDatabase.class, "logTable", " ********** BEGIN LOG TABLE (" + tableName + ") ********** " + UUString.safeString(message));
            UULog.debug(UUDatabase.class, "logTable", "columns: " + UUString.componentsJoinedByString(columns, ",") + ", where" + where + ", whereArgs: " + UUString.componentsJoinedByString(whereArgs, ","));
            UULog.debug(UUDatabase.class, "logTable", "There are " + c.getCount() + " records in " + tableName);
            logQueryResults(c);
            UULog.debug(UUDatabase.class, "logTable", " ********** END LOG TABLE (" + tableName + ") ********** " + UUString.safeString(message));
        }
        catch (Exception ex)
        {
            logException("logTable", ex);
        }
        finally
        {
            UUCloseable.safeClose(c);
        }
    }

    /**
     *
     * Logs all records matching the given query
     *
     * @param sql the raw query
     * @param message an optional message
     */
    public synchronized void logRawQuery(
        @NonNull final String sql,
        @Nullable final String message)
    {
        Cursor c = null;

        try
        {
            UUSQLiteDatabase db = getReadOnlyDatabase();

            c = db.rawQuery(sql, null);

            UULog.debug(UUDatabase.class, "logTable", " ********** BEGIN LOG QUERY (" + sql + ") ********** " + UUString.safeString(message));
            //UULog.debug(UUDatabase.class, "logTable", "columns: " + UUString.componentsJoinedByString(columns, ",") + ", where" + where + ", whereArgs: " + UUString.componentsJoinedByString(whereArgs, ","));
            UULog.debug(UUDatabase.class, "logTable", "There are " + c.getCount() + " records matching the query");
            logQueryResults(c);
            UULog.debug(UUDatabase.class, "logTable", " ********** END LOG TABLE (" + sql + ") ********** " + UUString.safeString(message));
        }
        catch (Exception ex)
        {
            logException("logTable", ex);
        }
        finally
        {
            UUCloseable.safeClose(c);
        }
    }

    /**
     * Logs the results of a query
     *
     * @param c a cursor obtained from a SQL command
     */
    public void logQueryResults(@NonNull final Cursor c)
    {
        try
        {
            c.moveToPosition(-1);

            while (c.moveToNext())
            {
                int columnCount = c.getColumnCount();
                HashMap<String, String> map = new LinkedHashMap<>();

                for (int i = 0; i < columnCount; i++)
                {
                    String key = c.getColumnName(i);

                    String val;

                    Object rawVal = UUCursor.safeGet(c, i, null);
                    if (rawVal == null)
                    {
                        val = "<null>";
                    }
                    else if (rawVal instanceof byte[])
                    {
                        val = UUString.byteToHex((byte[])rawVal);
                    }
                    else
                    {
                        val = rawVal.toString();
                    }

                    map.put(key, val);
                }

                UULog.debug(UUDatabase.class, "logQueryResults", map.toString());

            }
        }
        catch (Exception ex)
        {
            logException("logQueryResults", ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Safely ends a DB transaction
     *
     * @param db the database to end a transaction on
     */
    public void safeEndTransaction(@Nullable final UUSQLiteDatabase db)
    {
        try
        {
            if (db != null)
            {
                db.endTransaction();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUDatabase.class,"safeEndTransaction", ex);
        }
    }

    public void safeClose(@Nullable final UUSQLiteDatabase db)
    {
        try
        {
            if (db != null)
            {
                db.close();
            }
        }
        catch (Exception ex)
        {
            UULog.debug(UUCloseable.class, "safeClose", ex);
        }
    }

    @NonNull
    public static <T extends UUDataModel> String tableNameForModel(@NonNull final Class<T> type)
    {
        try
        {
            T obj = type.newInstance();
            return obj.getTableName();
        }
        catch (Exception ex)
        {
            return UUDataModel.tableNameForClass(type);
        }
    }

    private void safeDeleteDatabase()
    {
        try
        {
            applicationContext.deleteDatabase(getDatabaseName());
        }
        catch (Exception ex)
        {
            logException("safeDeleteDatabase", ex);
        }
    }

    /**
     * Logs a line of SQL
     *
     * @param sql the sql statement to log
     */
    protected void logSql(@NonNull final String sql)
    {
        UULog.debug(getClass(), "logSql", sql);
    }

    /**
     * Logs an exception
     *
     * @param methodName an explanatory message to go along with the exception
     * @param exception the caught exception
     */
    protected void logException(@NonNull final String methodName, @NonNull final Exception exception)
    {
        UULog.error(getClass(), methodName, exception);
    }

    protected void logContentValues(@NonNull final String methodName, @NonNull final ContentValues cv)
    {
        HashMap<String, Object> map = new LinkedHashMap<>();

        for (Map.Entry<String,Object> e : cv.valueSet())
        {
            map.put(e.getKey(), e.getValue());
        }

        UULog.debug(getClass(), methodName, "ContentValues: " + map.toString());
    }
}