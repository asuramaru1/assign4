import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;


public class Bank {
    static CountDownLatch latch;
    public static int ACCOUNTS = 100;
    private static BlockingQueue<transaction> transactionsQueue;
    private static ArrayList<Worker> workers;
    private static ArrayList<Account> accountsList;

    private static int BIG_NUMBER = 100000;
    private transaction poison = new transaction(-1, -1, -1);

    public Bank(int accounts, int workersNum) {
        accountsList = new ArrayList<>();
        latch = new CountDownLatch(workersNum);
        transactionsQueue = new ArrayBlockingQueue<transaction>(BIG_NUMBER);
        for (int i = 0; i < accounts; i++)
            accountsList.add(new Account(i));
        initialiseWorkers(workersNum);


    }

    private void initialiseWorkers(int workersNum) {
        workers = new ArrayList<Worker>();
        for (int i = 0; i < workersNum; i++) {
            workers.add(new Worker());
            workers.get(i).start();
        }
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    transaction nextTransaction = transactionsQueue.take();
                    if (nextTransaction.getFrom() == -1) {
                        transactionsQueue.add(poison);
                        latch.countDown();
                        break;
                    }
                    Account accto = accountsList.get(nextTransaction.getTo());
                    Account accfrom = accountsList.get(nextTransaction.getFrom());
                    accto.addBalance(nextTransaction.getAmount());
                    accfrom.withdraw(nextTransaction.getAmount());
                    System.out.println(accto);
                    System.out.println(accfrom);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }




    public void stopWorking() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("checking final balances please wait ...");
        for (int i = 0; i < accountsList.size(); i++) {
            System.out.println(accountsList.get(i));
        }
        System.out.println("bank finished working today , come back 2moro :)");
    }

    public void startWorking(String filename) throws IOException, InterruptedException {
        BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
        while (true) {
            String line = br.readLine();
            if (line == null) {
                transactionsQueue.put(poison);
                break;
            }
            String[] split = line.split(" ");
            transactionsQueue.put(new transaction(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
                    Integer.parseInt(split[2])));

        }
    }
}
