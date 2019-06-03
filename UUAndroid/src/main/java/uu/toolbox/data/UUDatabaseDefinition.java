package uu.toolbox.data;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import uu.toolbox.core.UUString;
import uu.toolbox.logging.UULog;

/**
 *
 * Interface for defining a SQL database definition
 */
public interface UUDatabaseDefinition
{
	default String getDatabaseName()
    {
        return databaseNameForClass(getClass());
    }

	default int getVersion()
    {
        return databaseVersionForClass(getClass());
    }

	default ArrayList<UUDataModel> getDataModels(int version)
    {
        return databaseModelsForClass(getClass(), version);
    }

	default void handlePostOpen(SQLiteDatabase db, int version)
	{

	}

	default void handlePostCreate(SQLiteDatabase db, int version)
	{

	}

	default void handlePostUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}

    @NonNull
    static String databaseNameForClass(@NonNull Class<?> databaseClass)
    {
        String name = null;

        UUSqlDatabase annotation = databaseClass.getAnnotation(UUSqlDatabase.class);
        if (annotation != null)
        {
            name = annotation.name();
        }

        if (UUString.isEmpty(name))
        {
            name = UUString.toSnakeCase(databaseClass.getSimpleName());
        }

        return name;
    }

    static int databaseVersionForClass(@NonNull Class<?> databaseClass)
    {
        int version = 1;

        UUSqlDatabase annotation = databaseClass.getAnnotation(UUSqlDatabase.class);
        if (annotation != null)
        {
            version = annotation.version();
        }

        return version;
    }

    @NonNull
    static ArrayList<UUDataModel> databaseModelsForClass(@NonNull Class<?> databaseClass, final int version)
    {
        ArrayList<UUDataModel> list = new ArrayList<>();

        try
        {
            UUSqlDatabase annotation = databaseClass.getAnnotation(UUSqlDatabase.class);
            if (annotation != null)
            {
                Class[] models = annotation.models();
                for (Class<?> c : models)
                {
                    if (UUDataModel.class.isAssignableFrom(c))
                    {
                        UUSqlTable tableAnnotation = c.getAnnotation(UUSqlTable.class);
                        if (tableAnnotation != null)
                        {
                            if (version >= tableAnnotation.existsInVersion())
                            {
                                list.add((UUDataModel)c.newInstance());
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUDatabaseDefinition.class, "databaseModelsForClass", ex);
        }

        return list;
    }
}