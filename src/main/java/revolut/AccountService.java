package revolut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *  singleton service to make the call to do the transfer
 */
public class AccountService {

    Map<String, Account> accounts = new HashMap<>();    // populated just for test purposes here
    Set<String> currentlyInTransferAccountsSet = new HashSet<>();   // contains a set of in transfer accounts under lock

    Lock lock = new ReentrantLock();

    public  AccountService() {
        // for test purposes here only - adding a few accounts
        // in real world accounts will be fetched from a possibly external service
        accounts.put("a1", new Account("a1", 100, System.currentTimeMillis()));
        accounts.put("a2", new Account("a2", 100, System.currentTimeMillis()));
    }


    private Account getAccount(String id) {
        // actual call
        return accounts.get(id);    // in reality, probably calls an external service to fetch the account
    }


    /**
     * do the actual transfer from one account to another. this is threadsafe
     * @param fromAccountId unique ID
     * @param toAccountId unique ID
     * @param amount
     * @return
     */
    public boolean transfer(String fromAccountId, String toAccountId, double amount) throws InterruptedException {

        System.out.println("Transfer from " + fromAccountId + " - " + toAccountId);

        Account fromAccount = null;
        Account toAccount = null;

        fromAccount = getAccount(fromAccountId);
        toAccount = getAccount(toAccountId);

        // check if accounts are valid
        if (fromAccount == null || toAccount == null) {
            return false;
        }

        if(lock.tryLock(2, TimeUnit.SECONDS)) {    // use top level lock object to prevent deadlocks. in case 1 thread transfers from A -> B and another transfers from B -> A
            try {
                // acquire locks on both accounts
                if (fromAccount.lock.tryLock(2, TimeUnit.SECONDS)) {
                    if (toAccount.lock.tryLock(2, TimeUnit.SECONDS)) {
                        // release lock on service lock
                        lock.unlock();
                        // proceed to do the transfer
                        try {
                            System.out.println("Transfer from " + fromAccountId + " - " + toAccountId + " DONE");
                            return fromAccount.transferTo(toAccount, amount);
                        } finally {
                            // release locks in order
                            toAccount.lock.unlock();
                            fromAccount.lock.unlock();
                        }
                    } else {
                        fromAccount.lock.unlock();
                        return false;
                    }
                } else {
                    lock.unlock();
                    return false;
                }
            } catch (Throwable e) {
                // log e
                // catch anything and exit gracefully
                return false;
            } finally {
                // nothing
            }

        }
        return false;

    }
}
