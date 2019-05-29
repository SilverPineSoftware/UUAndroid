package uu.toolbox.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

/**
 * Facade to wrap SQLiteDatabase
 */
public interface UUSQLiteDatabase
{
    void beginTransaction();
    void setTransactionSuccessful();
    void endTransaction();
    void close();

    void execSQL(String sql, Object[] bindArgs);

    Cursor query(String table, String[] columns, String selection,
                 String[] selectionArgs, String groupBy, String having,
                 String orderBy, String limit);

    int delete(String table, String whereClause, String[] whereArgs);

    Cursor rawQuery(String sql, String[] selectionArgs);

    int update(String table, ContentValues values, String whereClause, String[] whereArgs);

    long replace(String table, String nullColumnHack, ContentValues initialValues);

    long insert(String table, String nullColumnHack, ContentValues values);

    int getVersion();

    UUSQLiteStatement compileStatement(@NonNull final String sql);
}
