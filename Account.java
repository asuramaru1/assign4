public class Account {
    private double balance;
    private int transactionsMade;
    private int id;

    public Account(int ind, double balance, int transactionsMade) {
        this.transactionsMade = transactionsMade;
        this.id = id;
        this.balance = balance;
    }
    public  Account(int id) {
        this.transactionsMade = 0;
        this.id = id;
        this.balance = 1000;
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
        //if (!(amount <= balance)) throw new AssertionError();
        balance -= amount;
        transactionsMade++;
    }

    @Override
    public synchronized String toString() {
        return "account:  " + id + "  balance : " + balance + "  trans: " + transactionsMade;
    }
}