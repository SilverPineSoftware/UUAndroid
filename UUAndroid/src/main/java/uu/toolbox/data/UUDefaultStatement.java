package uu.toolbox.data;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

public class UUDefaultStatement implements UUSQLiteStatement
{
    private SQLiteStatement statement;

    UUDefaultStatement(@NonNull final SQLiteStatement statement)
    {
        this.statement = statement;
    }

    @Override
    public void bindString(final int index, @NonNull final String value)
    {
        statement.bindString(index, value);
    }

    @Override
    public void bindLong(final int index, final long value)
    {
        statement.bindLong(index, value);
    }

    @Override
    public void bindDouble(final int index, final double value)
    {
        statement.bindDouble(index, value);
    }

    @Override
    public void bindBlob(final int index, @NonNull final byte[] value)
    {
        statement.bindBlob(index, value);
    }

    @Override
    public void bindNull(final int index)
    {
        statement.bindNull(index);
    }

    @Override
    public void execute()
    {
        statement.execute();
    }

    @Override
    public long executeInsert()
    {
        return statement.executeInsert();
    }

    @Override
    public int executeUpdateDelete()
    {
        return statement.executeUpdateDelete();
    }

    @Override
    public void clearBindings()
    {
        statement.clearBindings();
    }

    @Override
    public void close()
    {
        statement.close();
    }
}
