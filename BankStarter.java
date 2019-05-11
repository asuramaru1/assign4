import java.io.IOException;

public class BankStarter {

    public static void main(String[] args) throws IOException, InterruptedException {
        String filename = "100k.txt";
        int workers = 10;
        try {
            workers = Integer.parseInt(args[1]);
            filename = args[0];


        } catch (Exception e) {
            System.out.println("invalid arguments ");
        }
        Bank bank = new Bank(Bank.ACCOUNTS, workers);
        bank.startWorking(filename);
        bank.stopWorking();
    }
}
