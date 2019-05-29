package uu.toolbox.network;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UUHttpTests
{
    private static final String ROOT_URL = "https://swapi.co/api";

    @Test
    public void test_0000_get() throws Exception
    {
        final CountDownLatch latch = new CountDownLatch(1);

        UUHttpRequest request = new UUHttpRequest(ROOT_URL, UUHttpMethod.GET);

        UUHttp.execute(request, response ->
        {
            Assert.assertNotNull(response);
            Assert.assertNull(response.getException());
            Assert.assertNotNull(response.getParsedResponse());
            Assert.assertEquals(200, response.getHttpResponseCode());

            latch.countDown();
        });

        latch.await();
    }

    @Test
    public void test_0001_getWithPathArgs() throws Exception
    {
        final CountDownLatch latch = new CountDownLatch(1);

        UUHttpRequest request = new UUHttpRequest(ROOT_URL, UUHttpMethod.GET);
        request.addQueryPathArgument("planets");
        request.addQueryPathArgument("1");

        UUHttp.execute(request, response ->
        {
            Assert.assertNotNull(response);
            Assert.assertNull(response.getException());
            Assert.assertNotNull(response.getParsedResponse());
            Assert.assertEquals(200, response.getHttpResponseCode());

            latch.countDown();
        });

        latch.await();
    }

    @Test
    public void test_0001_getWithQueryArgs() throws Exception
    {
        final CountDownLatch latch = new CountDownLatch(1);

        UUHttpRequest request = new UUHttpRequest(ROOT_URL + "/people", UUHttpMethod.GET);
        request.addQueryArgument("search", "luke");

        UUHttp.execute(request, response ->
        {
            Assert.assertNotNull(response);
            Assert.assertNull(response.getException());
            Assert.assertNotNull(response.getParsedResponse());
            Assert.assertEquals(200, response.getHttpResponseCode());

            latch.countDown();
        });

        latch.await();
    }
    @Test
    public void test_0001_getWithQueryAndPathArgs() throws Exception
    {
        final CountDownLatch latch = new CountDownLatch(1);

        UUHttpRequest request = new UUHttpRequest(ROOT_URL, UUHttpMethod.GET);
        request.addQueryPathArgument("people");
        request.addQueryArgument("search", "han");

        UUHttp.execute(request, response ->
        {
            Assert.assertNotNull(response);
            Assert.assertNull(response.getException());
            Assert.assertNotNull(response.getParsedResponse());
            Assert.assertEquals(200, response.getHttpResponseCode());

            latch.countDown();
        });

        latch.await();
    }

}
