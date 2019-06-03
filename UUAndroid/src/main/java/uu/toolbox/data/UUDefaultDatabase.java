package uu.toolbox.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

public abstract class UUDefaultDatabase extends UUDatabase
{
    protected UUDefaultDatabase(@NonNull final Context context, @NonNull final UUDatabaseDefinition databaseDefinition)
    {
        super(context, databaseDefinition);
    }

    @NonNull
    @Override
    protected UUSQLiteDatabase openDatabase()
    {
        OpenHelper helper = new OpenHelper(getApplicationContext(), getDatabaseDefinition());
        SQLiteDatabase db = helper.getWritableDatabase();
        return new Database(db);
    }

    private final class OpenHelper extends SQLiteOpenHelper
    {
        private OpenHelper(Context context, UUDatabaseDefinition databaseDefinition)
        {
            super(context, databaseDefinition.getDatabaseName(), null, databaseDefinition.getVersion());
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            handleCreate(new Database(db));
        }

        @Override
        public void onOpen(SQLiteDatabase db)
        {
            handleOpen(new Database(db));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            handleMigrate(new Database(db), oldVersion, newVersion);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            handleMigrate(new Database(db), oldVersion, newVersion);
        }
    }

    private final class Database implements UUSQLiteDatabase
    {
        private SQLiteDatabase db;

        private Database(SQLiteDatabase db)
        {
            this.db = db;
        }

        @Override
        public void beginTransaction()
        {
            db.beginTransaction();
        }

        @Override
        public void setTransactionSuccessful()
        {
            db.setTransactionSuccessful();
        }

        @Override
        public void endTransaction()
        {
            db.endTransaction();
        }

        @Override
        public void execSQL(String sql, Object[] bindArgs)
        {
            if (bindArgs == null)
            {
                db.execSQL(sql);
            }
            else
            {
                db.execSQL(sql, bindArgs);
            }
        }

        @Override
        public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
        {
            return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        }

        @Override
        public int delete(String table, String whereClause, String[] whereArgs)
        {
            return db.delete(table, whereClause, whereArgs);
        }

        @Override
        public Cursor rawQuery(String sql, String[] selectionArgs)
        {
            return db.rawQuery(sql, selectionArgs);
        }

        @Override
        public int update(String table, ContentValues values, String whereClause, String[] whereArgs)
        {
            return db.update(table, values, whereClause, whereArgs);
        }

        @Override
        public long replace(String table, String nullColumnHack, ContentValues initialValues)
        {
            return db.replace(table, nullColumnHack, initialValues);
        }

        @Override
        public long insert(String table, String nullColumnHack, ContentValues values)
        {
            return db.insert(table, nullColumnHack, values);
        }

        @Override
        public int getVersion()
        {
            return db.getVersion();
        }

        @Override
        public void close()
        {
            db.close();
        }

        @Override
        public UUSQLiteStatement compileStatement(@NonNull final String sql)
        {
            SQLiteStatement statement = db.compileStatement(sql);
            return new UUDefaultStatement(statement);
        }
    }
}
