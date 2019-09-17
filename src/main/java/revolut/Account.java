package revolut;

import org.multiverse.api.StmUtils;
import org.multiverse.api.references.TxnDouble;
import org.multiverse.api.references.TxnLong;

import java.util.concurrent.Callable;

public class Account {

    String id;
    TxnDouble balance;
    TxnLong lastUpdate;

    public Account(String id, TxnDouble balance, TxnLong lastUpdate) {
        this.id = id;
        this.balance = balance;
        this.lastUpdate = lastUpdate;
    }

    /**
     *  uses multiverse library to the math operations as a transaction atomically. this is thread safe and deadlock safe
     * @param other
     * @param amount
     * @return
     */
    public boolean transferTo(Account other, double amount) {
        return StmUtils.atomic(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    long date = System.currentTimeMillis();
                    Account.this.update(-amount, date);
                    other.update(amount, date);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        });
    }

    /**
     * atomic update
     * @param amount
     * @param date
     */
    private void update(double amount, long date) {
        StmUtils.atomic(() -> {
            balance.incrementAndGet(amount);
            lastUpdate.set(date);

            if (balance.get() <= 0) {
                throw new IllegalArgumentException("Not enough balance");
            }
        });
    }
}
