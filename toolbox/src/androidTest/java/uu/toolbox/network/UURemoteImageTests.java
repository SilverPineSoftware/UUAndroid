package uu.toolbox.network;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ApplicationTestCase;

import junit.framework.Assert;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import uu.toolbox.data.UUDataCache;
import uu.toolbox.network.UURemoteData;
import uu.toolbox.network.UURemoteImage;

public class UURemoteImageTests extends ApplicationTestCase<Application>
{
    private static String TEST_URL = "http://publicdomainarchive.com/?ddownload=47473";

    private static UURemoteImage dataCache;

    private CountDownLatch countdownLatch;

    public UURemoteImageTests()
    {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        if (dataCache == null)
        {
            UUDataCache.init(getContext().getApplicationContext());
            UURemoteData.init(getContext().getApplicationContext());
            UURemoteImage.init(getContext().getApplicationContext());
            dataCache = UURemoteImage.sharedInstance();
            UUDataCache.sharedInstance().clearCache();
        }
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void test_0000_fetchWhenDoesNotExist() throws Exception
    {
        countdownLatch = new CountDownLatch(1);

        final String key = TEST_URL;

        IntentFilter filter = new IntentFilter();
        filter.addAction(UURemoteData.Notifications.DataDownloaded);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        BroadcastReceiver br = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Assert.assertEquals("Expect success notification", UURemoteData.Notifications.DataDownloaded, intent.getAction());
                Assert.assertTrue("Expect remote path to exist in bundle extras", intent.hasExtra(UURemoteData.NotificationKeys.RemotePath));
                Assert.assertEquals("Expect remote path to be correct in bundle extras", key, intent.getStringExtra(UURemoteData.NotificationKeys.RemotePath));
                Assert.assertFalse("Expect no error to exist in bundle extras", intent.hasExtra(UURemoteData.NotificationKeys.Error));

                HashMap<String, Object> md = UURemoteData.sharedInstance().getMetaData(key);
                Assert.assertNotNull("Expect meta data after download to not be null", md);

                Bitmap data = UURemoteImage.sharedInstance().getImage(key, false);
                Assert.assertNotNull("Expect data after download to not be null", data);

                countdownLatch.countDown();
            }

        };

        lbm.registerReceiver(br, filter);

//        HashMap<String, Object> md = UURemoteData.sharedInstance().getMetaData(key);
//        Assert.assertNull("Expect meta data to be null before download", md);

        Bitmap data = UURemoteImage.sharedInstance().getImage(key, false);
        Assert.assertNull("Expect immediate data return to be null when object does not exist in cache", data);


        countdownLatch.await();

        lbm.unregisterReceiver(br);
    }

    /*
    public void test_0001_fetchFromBadUrl() throws Exception
    {
        countdownLatch = new CountDownLatch(1);

        final String key = "http://this.is.a.fake.url/non_existent.jpg";

        IntentFilter filter = new IntentFilter();
        filter.addAction(UURemoteData.Notifications.DataDownloadFailed);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getContext());
        BroadcastReceiver br = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Assert.assertEquals("Expect success notification", UURemoteData.Notifications.DataDownloadFailed, intent.getAction());
                Assert.assertTrue("Expect remote path to exist in bundle extras", intent.hasExtra(UURemoteData.NotificationKeys.RemotePath));
                Assert.assertEquals("Expect remote path to be correct in bundle extras", key, intent.getStringExtra(UURemoteData.NotificationKeys.RemotePath));

                //Assert.assertFalse("Expect no error to exist in bundle extras", intent.hasExtra(UURemoteData.NotificationKeys.Error));

                //HashMap<String, Object> md = UURemoteData.sharedInstance().getMetaData(key);
                //Assert.assertNull("Expect meta data to be null after failed download", md);

                byte[] data = UURemoteData.sharedInstance().getData(key);
                Assert.assertNull("Expect data to be null after failed download", data);

                countdownLatch.countDown();
            }

        };

        lbm.registerReceiver(br, filter);

//        HashMap<String, Object> md = UURemoteData.sharedInstance().getMetaData(key);
//        Assert.assertNull("Expect meta data to be null before download", md);

        byte[] data = UURemoteData.sharedInstance().getData(key);
        Assert.assertNull("Expect immediate data return to be null when object does not exist in cache", data);

        countdownLatch.await();

        lbm.unregisterReceiver(br);
    }*/

    /*
    public void test_0002_fetchExisting() throws Exception
    {
        final String key = TEST_URL;

        HashMap<String, Object> md = UURemoteData.sharedInstance().getMetaData(key);
        Assert.assertNotNull("Expect meta data after download to not be null", md);

        byte[] data = UURemoteData.sharedInstance().getData(key);
        Assert.assertNotNull("Expect data after download to not be null", data);
    }*/
}
