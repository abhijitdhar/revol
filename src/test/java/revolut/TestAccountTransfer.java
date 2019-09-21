package revolut;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.core.Application;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestAccountTransfer extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig() {
            {
                register(Resource.class);
                register(new Binder());
            }
        };
    }

    @Test
    public void testMultiThreadedTransfers() throws InterruptedException {
        ExecutorService ex = Executors.newFixedThreadPool(2);
        AccountService accountService = new AccountService();
        ex.submit(new Runnable() {
            @Override
            public void run() {
                accountService.transfer("a1", "a2", 50);
            }
        });

        ex.submit(new Runnable() {
            @Override
            public void run() {
                accountService.transfer("a2", "a1", 50);
            }
        });

        ex.awaitTermination(2, TimeUnit.SECONDS);
        ex.shutdown();

        Assert.assertEquals(100.0, accountService.accounts.get("a1").balance, 0);
        Assert.assertEquals(100.0, accountService.accounts.get("a2").balance, 0);
    }

    @Test
    public void testTransferAsRESTCall() {

        String response = target("/revolut/transfer")
                .queryParam("fromAccountId", "a1")
                .queryParam("toAccountId", "a2")
                .queryParam("amount", 10)
                .request()
                .get(String.class);


        JSONObject jsonResult = new JSONObject(response);       // ok to transfer amount = 10

        Assert.assertTrue(jsonResult.optBoolean("result"));

        response = target("/revolut/transfer")
                .queryParam("fromAccountId", "a1")
                .queryParam("toAccountId", "a2")
                .queryParam("amount", 200)
                .request()
                .get(String.class);


        jsonResult = new JSONObject(response);

        Assert.assertFalse(jsonResult.optBoolean("result"));    // not ok to transfer amount = 200. goes over limit


        response = target("/revolut/transfer")
                .queryParam("fromAccountId", "a1")
                .queryParam("toAccountId", "a3")        // a3 is invalid account
                .queryParam("amount", 2)
                .request()
                .get(String.class);


        jsonResult = new JSONObject(response);

        Assert.assertFalse(jsonResult.optBoolean("result"));    // not ok to transfer from invalid account

    }

}
