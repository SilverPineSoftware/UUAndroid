package uu.toolbox.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import uu.toolbox.core.UUString;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class UUSql
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Constants
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String ROWID_COLUMN = "rowid";

    public static final int MAX_BOUND_STATEMENTS = 999;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static String buildCreateSql(final UUDataModel dataModel, final int version)
    {
        return buildCreateSql(dataModel.getTableName(), dataModel.getColumnMap(version), dataModel.getPrimaryKeyColumnName());
    }

    public static String buildCreateSql(final String tableName, final Map<Object, Object> columnDefs, final String primaryKey)
    {
        return buildCreateSql(tableName, columnDefs, primaryKey, false);
    }

    public static String buildCreateSql(final String tableName, final Map<Object, Object> columnDefs, final String primaryKey, final boolean isTempTable)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE");

        if (isTempTable)
        {
            sb.append(" TEMP");
        }

        sb.append(" TABLE ");
        sb.append(tableName);
        sb.append(" (");

        boolean first = true;
        for (Object col : columnDefs.keySet())
        {
            // Super special case.  SQLite has a built in rowid, so if a model defines a column
            // named 'rowid', we skip creating our own column.  This allows the data model to
            // query the built in SQLite rowid rather than an arbitrary user created column.
            if (ROWID_COLUMN == col)
            {
                continue;
            }

            sb.append(first ? "" : ", ");
            Object dataType = columnDefs.get(col);
            sb.append(col.toString());
            sb.append(" ");
            sb.append(dataType.toString());
            first = false;
        }

        if (primaryKey != null)
        {
            sb.append(", PRIMARY KEY(");
            sb.append(primaryKey);
            sb.append(")");
        }

        sb.append(");");

        return sb.toString();
    }

    public static void appendUpgradeTableSql(@NonNull final ArrayList<String> list,
                                             @NonNull final UUDataModel dataModel,
                                             final int fromVersion,
                                             final int toVersion)
    {
        String tableName = dataModel.getTableName();

        HashMap<Object, Object> fromColumns = dataModel.getColumnMap(fromVersion);
        HashMap<Object, Object> toColumns = dataModel.getColumnMap(toVersion);

        Set<Object> fromColumnNames = fromColumns.keySet();
        Set<Object> toColumnNames = toColumns.keySet();

        ArrayList<Object> removedColumns = new ArrayList<>();
        ArrayList<Object> addedColumns = new ArrayList<>();

        for (Object fromColumn : fromColumnNames)
        {
            if (!toColumnNames.contains(fromColumn))
            {
                removedColumns.add(fromColumn);
            }
        }

        for (Object toColumn : toColumnNames)
        {
            if (!fromColumnNames.contains(toColumn))
            {
                addedColumns.add(toColumn);
            }
        }

        if (removedColumns.size() > 0)
        {
            // SQLite does not support dropping columns, so if any columns are removed in the upgrade,
            // We have to create a temporary table with the new structure, then insert data into it,
            // and then drop the old table.

            String tmpTable = tableName + "_old_" + fromVersion;
            list.add(UUSql.buildRenameTableSql(tableName, tmpTable));
            list.add(UUSql.buildCreateSql(dataModel, toVersion));
            list.add(UUSql.buildSelectIntoTableCopy(tmpTable, tableName, getColumnNames(dataModel, toVersion)));
            list.add(UUSql.buildDropTableSql(tmpTable));
        }
        else
        {
            for (Object column : addedColumns)
            {
                list.add(buildAddColumnSql(tableName, column, toColumns.get(column)));
            }
        }
    }

    public static String buildSelectIntoTableCopy(final String srcTable, final String destTable, final Object[] columns)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(destTable);
        sb.append(" SELECT ");
        sb.append(UUString.componentsJoinedByString(columns, ", "));
        sb.append(" FROM ");
        sb.append(srcTable);
        sb.append(";");
        return sb.toString();
    }

    public static String buildRenameTableSql(final String existingTable, final String newTable)
    {
        return String.format(Locale.US, "ALTER TABLE %s RENAME TO %s;", existingTable, newTable);
    }

    public static String buildDropTableSql(final String tableName)
    {
        return String.format(Locale.US, "DROP TABLE IF EXISTS %s;", tableName);
    }

    public static String buildAddColumnSql(final String table, final Object column, final Object type)
    {
        return String.format(Locale.US, "ALTER TABLE %s ADD COLUMN %s %s;", table, column, type);
    }

    public static String buildCreateIndexSql(final String table, final String indexName, final Object column)
    {
        return String.format(Locale.US, "CREATE INDEX IF NOT EXISTS %s ON %s (%s);", indexName, table, column);
    }

    public static String buildDropIndexSql(final String indexName)
    {
        return String.format(Locale.US, "DROP INDEX IF EXISTS %s;", indexName);
    }

    public static String[] getColumnNames(final UUDataModel dataModel, final int version)
    {
        HashMap<Object, Object> columnMap = dataModel.getColumnMap(version);

        Object[] columnObjects = columnMap.keySet().toArray(new Object[columnMap.size()]);
        String[] list = new String[columnObjects.length];
        for (int i = 0; i < list.length; i++)
        {
            list[i] = columnObjects[i].toString();
        }

        return list;
    }

    public static String buildSingleColumnWhere(@NonNull final Object columnName)
    {
        return String.format(Locale.US, "%s = ?", columnName);
    }

    public static void appendCreateLines(@NonNull final ArrayList<String> list, @NonNull UUDatabaseDefinition databaseDefinition, final int version)
    {
        ArrayList<UUDataModel> tableDefs = databaseDefinition.getDataModels(version);
        if (tableDefs != null)
        {
            for (UUDataModel dataModel : tableDefs)
            {
                list.add(UUSql.buildCreateSql(dataModel, version));
            }
        }
    }

    private static boolean containsModelClass(@NonNull final ArrayList<UUDataModel> list, @NonNull final Class modelClass)
    {

        for (UUDataModel dataModel : list)
        {
            if (dataModel.getClass() == modelClass)
            {
                return true;
            }
        }

        return false;
    }

    public static void analyzeModelSets(
            @NonNull ArrayList<UUDataModel> fromModels,
            @NonNull ArrayList<UUDataModel> toModels,
            @NonNull ArrayList<UUDataModel> addedModels,
            @NonNull ArrayList<UUDataModel> deletedModels,
            @NonNull ArrayList<UUDataModel> migratedModels)
    {
        for (UUDataModel fromModel : fromModels)
        {
            if (containsModelClass(toModels, fromModel.getClass()))
            {
                migratedModels.add(fromModel);
            }
            else
            {
                deletedModels.add(fromModel);
            }
        }

        Iterator<UUDataModel> toListIterator = toModels.iterator();
        while (toListIterator.hasNext())
        {
            UUDataModel model = toListIterator.next();
            if (containsModelClass(migratedModels, model.getClass()))
            {
                toListIterator.remove();
            }
        }

        // Migrated models have been removed, so all that is left are new models
        addedModels.addAll(toModels);
    }

    public static void appendUpgradeLines(
            @NonNull final ArrayList<String> list,
            @NonNull UUDatabaseDefinition databaseDefinition,
            final int fromVersion,
            final int toVersion)
    {
        ArrayList<UUDataModel> fromModels = databaseDefinition.getDataModels(fromVersion);
        ArrayList<UUDataModel> toModels = databaseDefinition.getDataModels(toVersion);

        ArrayList<UUDataModel> addedModels = new ArrayList<>();
        ArrayList<UUDataModel> migratedModels = new ArrayList<>();
        ArrayList<UUDataModel> deletedModels = new ArrayList<>();

        analyzeModelSets(fromModels, toModels, addedModels, deletedModels, migratedModels);

        for (UUDataModel deletedModel : deletedModels)
        {
            list.add(UUSql.buildDropTableSql(deletedModel.getTableName()));
        }

        for (UUDataModel addedModel : addedModels)
        {
            list.add(UUSql.buildCreateSql(addedModel, toVersion));
        }

        for (UUDataModel migratedModel : migratedModels)
        {
            UUSql.appendUpgradeTableSql(list, migratedModel, fromVersion, toVersion);
        }
    }




    @NonNull
    public static String buildWhereClause(@Nullable final String where)
    {
        if (UUString.isNotEmpty(where))
        {
            return " WHERE " + where;
        }
        else
        {
            return "";
        }
    }

    @NonNull
    public static String buildCountSql(@NonNull final String tableName, @Nullable final String where)
    {
        return "SELECT COUNT(*) FROM " + tableName + buildWhereClause(where);
    }

    /**
     * Formats a SQLite limit clause from an offset and limit number
     *
     * @param offset the offset
     * @param limit the limit
     * @return a string that can be passed as the limit param to android db calls
     */
    @NonNull
    public static String formatLimitClause(final long offset, final long limit)
    {
        StringBuilder sb = new StringBuilder();
        if (offset != -1)
        {
            sb.append(offset);
        }

        if (sb.length() > 0)
        {
            sb.append(",");
            sb.append(limit);
        }
        else if (limit > 0)
        {
            sb.append(limit);
        }

        return sb.toString();
    }

    /**
     * Formats a sort by clause with an ascending or descending qualifier
     *
     * @param column the column to sort on
     * @param ascending ascending or descending
     * @return a string that can be used as a sortBy/orderBy parameter in a SQL statement
     */
    @NonNull
    public static String formatSortByClause(@NonNull final String column, final boolean ascending)
    {
        return column + (ascending ? " ASC" : " DESC");
    }

    /**
     * Formats a string as a list of question marks that can be used as the parameter list in a SQL
     * statement
     *
     * @param count number of question marks
     *
     * @return a string of question marks seperated by commas
     */
    @NonNull
    public static String buildParameterList(final int count)
    {
        String[] list = new String[count];
        for (int i = 0; i < count; i++)
        {
            list[i] = "?";
        }

        return UUString.componentsJoinedByString(list, ",");
    }

    /**
     * Formats a string as a multi-dimensional array of question marks used to bind multiple sql
     * insert statements at once.
     *
     * @param recordCount number of records
     * @param fieldCount number of fields per record
     * @return a string
     */
    @NonNull
    public static String buildMultiRecordParameterList(final int recordCount, final int fieldCount)
    {
        String fieldLine = "(" + buildParameterList(fieldCount) + ")";

        String[] list = new String[recordCount];
        for (int i = 0; i < recordCount; i++)
        {
            list[i] = fieldLine;
        }

        return UUString.componentsJoinedByString(list, ",");
    }

    /**
     * Formats a SQL WHERE IN statement on a single column
     *
     * @param column the column
     * @param list the list of values
     * @return sql args
     */
    @NonNull
    public static UUSqlArgs formatWhereInClause(@NonNull final String column, @NonNull final ArrayList<String> list)
    {
        UUSqlArgs args = new UUSqlArgs();
        args.where = String.format(Locale.US, "%s IN (%s)", column, buildParameterList(list.size()));
        args.whereArgs = list.toArray(new String[list.size()]);
        return args;
    }

    /**
     * Formats a SQL WHERE NOT IN statement on a single column
     *
     * @param column the column
     * @param list the list of values
     * @return sql args
     */
    @NonNull
    public static UUSqlArgs formatWhereNotInClause(@NonNull final String column, @NonNull final ArrayList<String> list)
    {
        UUSqlArgs args = new UUSqlArgs();
        args.where = String.format(Locale.US, "%s NOT IN (%s)", column, buildParameterList(list.size()));
        args.whereArgs = list.toArray(new String[list.size()]);
        return args;
    }

    /**
     * Combines a list of where args into a single where statement
     *
     * @param list the list of args
     * @param operator the operator to combine with
     * @return more args
     */
    @NonNull
    public static UUSqlArgs combineWhereArgs(@NonNull final ArrayList<UUSqlArgs> list, @NonNull final String operator)
    {
        if (list.size() == 1)
        {
            return list.get(0);
        }

        StringBuilder where = new StringBuilder();
        ArrayList<String> whereArgs = new ArrayList<>();

        for (UUSqlArgs arg : list)
        {
            if (UUString.isNotEmpty(arg.where))
            {
                if (where.length() > 0)
                {
                    where.append(" ");
                    where.append(operator);
                    where.append(" ");
                }

                where.append("(");
                where.append(arg.where);
                where.append(")");

                if (arg.whereArgs != null && arg.whereArgs.length > 0)
                {
                    whereArgs.addAll(Arrays.asList(arg.whereArgs));
                }
            }
        }

        UUSqlArgs args = new UUSqlArgs();
        args.where = where.toString();

        if (whereArgs.size() > 0)
        {
            args.whereArgs = whereArgs.toArray(new String[whereArgs.size()]);
        }

        return args;
    }

    /**
     * Combines a list of arguments with an AND clause
     * @param list the list of args
     * @return args
     */
    @NonNull
    public static UUSqlArgs combineWithAnd(@NonNull final ArrayList<UUSqlArgs> list)
    {
        return combineWhereArgs(list, "AND");
    }

    /**
     * Combines a list of arguments with an AND clause
     * @param list the list of args
     * @return args
     */
    @NonNull
    public static UUSqlArgs combineWithOr(@NonNull final ArrayList<UUSqlArgs> list)
    {
        return combineWhereArgs(list, "OR");
    }

    /**
     * Builds a multi-column sort clause
     *
     * @param sortClauses list of sort clauses
     * @return string to be used in a SQL ORDER by statement
     */
    public static String buildSortClause(@NonNull final ArrayList<UUSqlSort> sortClauses)
    {
        ArrayList<Object> lines = new ArrayList<>();

        for (UUSqlSort sort : sortClauses)
        {
            lines.add(formatSortByClause(sort.column, sort.ascending));
        }

        return UUString.componentsJoinedByString(lines, ",");
    }

    public static String buildOptimizeFullTextIndexSql(@NonNull final String tableName)
    {
        return String.format(Locale.US, "INSERT INTO %s(%s) VALUES ('optimize');", tableName, tableName);
    }
}