package revolut;

import org.json.simple.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/revolut")
@Produces({MediaType.APPLICATION_JSON})
public class Resource {

    @Inject
    AccountService accountService;

    /**
     *  takes a from and to account id uuids and looks up the accounts from an external service and make the transfer
     * @param fromAccountId
     * @param toAccountId
     * @param amount
     * @return
     */
    @GET
    @Path("/transfer")
    @Produces("application/json")
    public String transfer(@QueryParam("fromAccountId") String fromAccountId, @QueryParam("toAccountId") String toAccountId, @QueryParam("amount") double amount) throws InterruptedException {
        boolean result = accountService.transfer(fromAccountId, toAccountId, amount);
        JSONObject obj = new JSONObject();
        obj.put("result", result);
        return  obj.toJSONString();

    }
}
