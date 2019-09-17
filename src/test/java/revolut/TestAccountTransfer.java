package revolut;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;

import javax.ws.rs.core.Application;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

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
    public void testTransfer() {

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
