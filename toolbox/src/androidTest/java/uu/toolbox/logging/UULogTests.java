package uu.toolbox.logging;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UULogTests
{
    @Test
    public void test_0000_testWrap()
    {
        StringBuilder sb = new StringBuilder();

        while (sb.length() < 5000)
        {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
        }

        UULog.debug(getClass(), "test_0000_testWrap", sb.toString());
    }
}
