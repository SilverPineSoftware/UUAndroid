package uu.toolbox.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import uu.toolbox.logging.UULog;

/**
 * UUFile
 * Useful Utilities - UUFile is a lightweight wrapper on simple file operations
 *
 * License:
 * You are free to use this code for whatever purposes you desire.
 * The only requirement is that you smile everytime you use it. :-)
 */
public class UUFile
{
    private static final int CHUNK_SIZE = 10240; // 10k

    /**
     * Reads the entire contents of a file
     *
     * @param file the file to read
     * @return contents of the file
     */
    @Nullable
    public static byte[] readFile(@NonNull final File file)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileInputStream fis = null;
        byte[] result = null;

        try
        {
            if (file.exists())
            {
                fis = new FileInputStream(file);

                byte[] buffer = new byte[CHUNK_SIZE];

                int bytesRead;

                do
                {
                    bytesRead = fis.read(buffer, 0, buffer.length);
                    if (bytesRead > 0)
                    {
                        bos.write(buffer, 0, bytesRead);
                    }
                }
                while (bytesRead > 0);

                result = bos.toByteArray();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUFile.class, "readFile", ex);
            result = null;
        }
        finally
        {
            closeStream(fis);
            closeStream(bos);
        }

        return result;
    }

    /**
     * Writes data to a file, overwriting its entire contents
     *
     * @param file the file to write
     * @param data the data to write
     *
     * @return boolean indicating success or failure
     */
    public static boolean writeFile(@NonNull final File file, @NonNull byte[] data)
    {
        ByteArrayInputStream bis = null;
        FileOutputStream fos = null;

        try
        {
            bis = new ByteArrayInputStream(data);
            fos = new FileOutputStream(file);

            byte[] buffer = new byte[CHUNK_SIZE];

            int bytesRead;

            do
            {
                bytesRead = bis.read(buffer, 0, buffer.length);
                if (bytesRead > 0)
                {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            while (bytesRead > 0);

            return true;
        }
        catch (Exception ex)
        {
            UULog.error(UUFile.class, "writeFile", ex);
            return false;
        }
        finally
        {
            closeStream(bis);
            closeStream(fos);
        }
    }

    /**
     * Copies a file
     *
     * @param src the file to copy
     * @param dest the destination
     */
    public static void copyFile(@NonNull final File src, @NonNull final File dest)
    {
        deleteFile(dest);

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try
        {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dest);

            byte[] buffer = new byte[CHUNK_SIZE];

            int bytesRead;

            do
            {
                bytesRead = fis.read(buffer, 0, buffer.length);
                if (bytesRead > 0)
                {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            while (bytesRead > 0);

            fos.flush();
        }
        catch (Exception ex)
        {
            UULog.error(UUFile.class, "copyFile", ex);
        }
        finally
        {
            UUCloseable.safeClose(fis);
            UUCloseable.safeClose(fos);
        }
    }

    /**
     * Deletes a file from disk, or if the file is a folder, recursively deletes everything under
     * that folder
     *
     * @param file the file to delete
     * @return result of file.delete or false if an expection is thrown
     */
    public static boolean deleteFile(@NonNull final File file)
    {
        try
        {
            if (file.isDirectory())
            {
                File[] files = file.listFiles();
                for (File f : files)
                {
                    boolean result = deleteFile(f);
                    if (!result)
                    {
                        return false;
                    }
                }
            }
            else
            {
                return file.delete();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUFile.class, "deleteFile", ex);
            return false;
        }

        return true;
    }

    /**
     * Safely creates any necessary folders
     *
     * @param file the folder to operate on
     */
    public static void createFoldersIfNeeded(@Nullable final File file)
    {
        try
        {
            if (file != null)
            {
                boolean result = file.mkdirs();
                UULog.debug(UUFile.class, "createFoldersIfNeeded", "mkdirs returned: " + result);
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUFile.class, "createFoldersIfNeeded", ex);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Safely close a stream
     *
     * @param closeable the object to close
     */
    private static void closeStream(@Nullable final Closeable closeable)
    {
        try
        {
            if (closeable != null)
            {
                closeable.close();
            }
        }
        catch (Exception ex)
        {
            UULog.error(UUFile.class, "closeStream", ex);
        }
    }
}
