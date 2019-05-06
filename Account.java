public class Account {
    private double balance;
    private int transactionsMade;
    private int id;

    public void Account(int ind, double balance, int transactionsMade) {
        this.transactionsMade = transactionsMade;
        this.id = id;
        this.balance = balance;
    }

    public void Account(int id) {
        Account(id, 1000, 0);
    }

    public synchronized void addBalance(double add) {
        balance += add;
        transactionsMade++;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public synchronized int getTransactionsMade() {
        return transactionsMade;
    }

    public synchronized void withdraw(double amount) {
        assert amount <= balance;
        balance -= amount;
        transactionsMade++;
    }

    @Override
    public synchronized String toString() {
        return "account:  " + id + "  balance : " + balance + "  trans: " + transactionsMade;
    }
}