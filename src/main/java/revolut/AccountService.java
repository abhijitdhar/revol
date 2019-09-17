package revolut;

import org.multiverse.stms.gamma.transactionalobjects.GammaTxnDouble;
import org.multiverse.stms.gamma.transactionalobjects.GammaTxnLong;

import java.util.HashMap;
import java.util.Map;

/**
 *  singleton service to make the call to do the transfer
 */
public class AccountService {

    Map<String, Account> accounts = new HashMap<>();    // populated just for test purposes here

    public  AccountService() {
        // for test purposes here only - adding a few accounts
        // in real world accounts will be fetched from a possibly external service
        accounts.put("a1", new Account("a1", new GammaTxnDouble(100), new GammaTxnLong(System.currentTimeMillis())));
        accounts.put("a2", new Account("a2", new GammaTxnDouble(100), new GammaTxnLong(System.currentTimeMillis())));
    }


    private Account getAccount(String id) {
        // actual call
        return accounts.get(id);
    }


    /**
     * do the actual transfer from one account to another
     * @param fromAccountId
     * @param toAccountId
     * @param amount
     * @return
     */
    public boolean transfer(String fromAccountId, String toAccountId, double amount) {
        // check if accounts are valid
        Account fromAccount = getAccount(fromAccountId);
        Account toAccount = getAccount(toAccountId);

        if(fromAccount == null || toAccount == null)  {
            return false;
        }

        // proceed to do the transfer
        return fromAccount.transferTo(toAccount, amount);
    }
}
