package revolut;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    Lock lock = new ReentrantLock();

    String id;  // assumes the IDs are unique
    double balance;
    long lastUpdate;

    public Account(String id, double balance, long lastUpdate) {
        this.id = id;
        this.balance = balance;
        this.lastUpdate = lastUpdate;
    }

    /**
     *  this is to be called from a threadsafe method
     * @param other
     * @param amount
     * @return
     */
    public boolean transferTo(Account other, double amount) {
        try {
            long date = System.currentTimeMillis();
            this.update(-amount, date);
            other.update(amount, date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * atomic update
     * @param amount
     * @param date
     */
    private void update(double amount, long date) {
        balance += amount;
        lastUpdate = date;

        if (balance <= 0) {
            throw new IllegalArgumentException("Not enough balance");
        }
    }
}
