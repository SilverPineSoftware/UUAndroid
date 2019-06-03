package uu.toolbox.data;

//
// Simple struct to hold arguments that will be passed to SQLite methods
//
public class UUSqlArgs
{
    public String where;
    public String[] whereArgs;
    public String orderBy;
    public String limit;

    // Used for cases when these will be passed to a raw execSql method.
    public Object[] bindArgs;
}
