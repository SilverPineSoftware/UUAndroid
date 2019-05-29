package uu.toolbox.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import uu.toolbox.logging.UULog;

@SuppressWarnings("unused")
public class UUDatabaseHelper extends SQLiteOpenHelper
{
	private UUDatabaseDefinition databaseDefinition;

    public UUDatabaseHelper(final Context context, final UUDatabaseDefinition definition)
	{
	    super(context, definition.getDatabaseName(), null, definition.getVersion());
	    databaseDefinition = definition;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	// SQLiteOpenHelper Methods
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		try
		{
            int version = databaseDefinition.getVersion();

            ArrayList<String> lines = new ArrayList<>();
            UUSql.appendCreateLines(lines, databaseDefinition, version);

            db.beginTransaction();

            for (String line : lines)
            {
                logSql(line);
                db.execSQL(line);
            }

            databaseDefinition.handlePostCreate(db, version);

            db.setTransactionSuccessful();
		}
		catch (Exception ex)
		{
			logException("onCreate", ex);
		}
        finally
        {
            safeEndTransaction(db);
        }
    }
	
	@Override
	public void onOpen(SQLiteDatabase db)
	{
	    super.onOpen(db);

        try
        {
            databaseDefinition.handlePostOpen(db, databaseDefinition.getVersion());
        }
        catch (Exception ex)
        {
            logException("onOpen", ex);
        }
	}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        migrateDatabase(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        migrateDatabase(db, oldVersion, newVersion);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
	// Private Methods
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	private void logSql(final String sql)
    {
		UULog.debug(getClass(), "logSql", sql);
    }
	
	private void logException(final String message, final Throwable throwable)
    {
    	UULog.error(getClass(), "logException", message, throwable);
    }

    private void safeEndTransaction(final SQLiteDatabase db)
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
            logException("safeEndTransaction", ex);
        }
    }

    private void migrateDatabase(@NonNull final SQLiteDatabase db, final int oldVersion, final int newVersion)
    {
        try
        {
            ArrayList<String> lines = new ArrayList<>();
            UUSql.appendUpgradeLines(lines, databaseDefinition, oldVersion, newVersion);

            db.beginTransaction();

            for (String line : lines)
            {
                logSql(line);
                db.execSQL(line);
            }

            databaseDefinition.handlePostUpgrade(db, oldVersion, newVersion);

            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            logException("migrateDatabase", ex);
        }
        finally
        {
            safeEndTransaction(db);
        }
    }
}