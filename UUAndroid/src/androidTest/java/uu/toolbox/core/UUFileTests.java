package uu.toolbox.core;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UUFileTests
{
    private static String TEST_FILE_NAME = "DummyFile.dat";

    private File getTestFile()
    {
        File file = new File(InstrumentationRegistry.getContext().getCacheDir(), TEST_FILE_NAME);
        Assert.assertNotNull("Expect file object to not be null", file);
        return file;
    }

    @Test
    public void test_0000_ReadFileThatDoesntExist() throws Exception
    {
        File file = getTestFile();
        if (file.exists())
        {
            boolean result = file.delete();
            Assert.assertTrue(result);
        }

        Assert.assertFalse("Expect file not to exist", file.exists());

        byte[] contents = UUFile.readFile(file);
        Assert.assertNull("Expect null data when file doesn't exist", contents);
    }

    @Test
    public void test_0001_DeleteFileThatDoesNotExist() throws Exception
    {
        File file = getTestFile();
        if (file.exists())
        {
            boolean result = file.delete();
            Assert.assertTrue(result);
        }

        Assert.assertFalse("Expect file not to exist", file.exists());

        boolean result = UUFile.deleteFile(file);
        Assert.assertFalse("Expect delete file on non existent file to return false", result);
    }

    @Test
    public void test_0002_WriteFileThatDoesntExist() throws Exception
    {
        File file = getTestFile();
        Assert.assertFalse("Expect file not to exist", file.exists());

        byte[] data = UURandom.randomBytes(256);
        boolean result = UUFile.writeFile(file, data);
        Assert.assertTrue("Expect write file to cache to work", result);
        Assert.assertTrue("Expect file to exist after writing", file.exists());
        Assert.assertTrue("Expect file to be readable after writing", file.canRead());
        Assert.assertTrue("Expect file to be writable after writing", file.canWrite());

        byte[] contents = UUFile.readFile(file);
        Assert.assertNotNull("Expect actual data when file does exist", contents);

        Assert.assertArrayEquals("Expect readFile to return same bytes that were written", contents, data);
    }

    @Test
    public void test_0003_DeleteFileThatDoesExist() throws Exception
    {
        File file = getTestFile();
        Assert.assertTrue("Expect file to exist", file.exists());

        boolean result = UUFile.deleteFile(file);
        Assert.assertTrue("Expect delete file to return true", result);
    }
}

