import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class WebFrame extends JFrame {
    private CountDownLatch cd;
    private DefaultTableModel model;
    long start ;
    private JTable table;
    private JPanel panel;
    private JButton single;
    private JButton concurent;
    private JButton stop ;
    private WebLauncher launcher;
    private JLabel running;
    private static final String  RUNNING_STR = "Running: ";
    private JLabel elapsed ;
    private static final String ELAPSED_STR = "ELapsed: ";
    private JLabel completed;
    private static final String COMPLETED_STR="Completed: ";
    private JTextField threadNum;
    private JProgressBar progressBar;
    private Semaphore workingWorkers;
    private Semaphore forRunning;
    private Semaphore forComplete;
    public int threadsRunning;
    public int threadsComplete;
    public WebFrame(){
        launcher = null;
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        model = fillModel();
        table = new JTable(model);
        threadsRunning=0;
        threadsComplete=0;
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setPreferredSize(new Dimension(600,300));

        panel.add(scrollpane);
        addJItems(panel);
        addListeners();
        add(panel);
        pack();
        setVisible(true);

    }

    private void addListeners() {
        single.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                clear();
                start = System.currentTimeMillis();
                launch(true);
                stop.setEnabled(true);
                concurent.setEnabled(false);
                single.setEnabled(false);
            }
        });
        concurent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                clear();
                start = System.currentTimeMillis();
                launch(false);
                stop.setEnabled(true);
                concurent.setEnabled(false);
                single.setEnabled(false);
            }
        });
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                launcher.interrupt();
                for(WebWorker w : launcher.workers)
                    w.interrupt();
                elapsed.setText(ELAPSED_STR+(System.currentTimeMillis()-start)+"ms");
                stop.setEnabled(false);
                concurent.setEnabled(true);
                single.setEnabled(true);


            }
        });



    }

    private void clear() {
        concurent.setEnabled(true);
        single.setEnabled(true);
        progressBar.setValue(0);
        elapsed.setText(ELAPSED_STR+"0");
        running.setText(RUNNING_STR+"0");
        completed.setText(COMPLETED_STR+"0");
        threadsRunning=0;
        threadsComplete=0;
        for(int i = 0 ; i<model.getRowCount() ; i++)
            model.setValueAt("",i,1);

    }

    private void launch(boolean singlee) {
        if(launcher!=null)launcher.interrupt();
        launcher = new WebLauncher(singlee , Integer.parseInt(threadNum.getText()) ,model.getRowCount() , this );
        launcher.start();
    }


    private void addJItems(JPanel panel) {
        single = new JButton("Single Thread Fetch");
        concurent = new JButton("Concurent Fetch");
        threadNum = new JTextField("4");
        threadNum.setMaximumSize(new Dimension(20,10));
        running = new JLabel(RUNNING_STR+"0");
        completed = new JLabel(COMPLETED_STR+"0");
        elapsed= new JLabel(ELAPSED_STR+"0.0");
        progressBar = new JProgressBar(0, model.getRowCount());
        stop = new JButton("Stop");
        panel.add(single);
        panel.add(concurent);
        panel.add(threadNum);
        panel.add(running);
        panel.add(completed);
        panel.add(elapsed);
        panel.add(progressBar);
        stop.setEnabled(false);
        panel.add(stop);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private DefaultTableModel fillModel() {
        DefaultTableModel result  = new DefaultTableModel(new String[] { "url", "status"}, 0);
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("links.txt")));
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                result.addRow(new String[]{line, ""});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void main(String[] args) {
        new WebFrame();
    }

    public void rowFinishedStatus(String downloadResult, int rowToUpdate , int col) {
        model.setValueAt(downloadResult,rowToUpdate,col);
        threadsRunningAdd(-1);
        workingWorkers.release();
        cd.countDown();
        try {
            forComplete.acquire();
            threadsComplete++;
            forComplete.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue(progressBar.getValue()+1);
                completed.setText(COMPLETED_STR+threadsComplete);
                elapsed.setText(ELAPSED_STR+(System.currentTimeMillis()-start)+"ms");
            }
        });

    }

    private class WebLauncher extends Thread{
        private ArrayList<WebWorker> workers ;
        private int rows;
        private WebFrame frame;
        public WebLauncher(boolean singleThread , int numThreads , int rows , WebFrame frame ){
            cd = new CountDownLatch(rows);
            this.rows = rows;
            int semMax = singleThread?1 : numThreads;
            workingWorkers = new Semaphore(semMax);
            forComplete = new Semaphore(1);
            forRunning = new Semaphore(1);
            workers = new ArrayList<>();
            this.frame = frame;
        }
        public void run(){
            threadsRunningAdd(1);
            for(int i = 0 ; i<rows ; i++){

                WebWorker wb = new WebWorker((String)model.getValueAt(i,0) ,i , frame);
                workers.add(wb);
            }
            for(WebWorker w : workers){
                if(isInterrupted()){
                    break;
                }
                try {
                    workingWorkers.acquire();
                } catch (InterruptedException e) {
                    break;
                }
                threadsRunningAdd(1);
                w.start();
            }
            try {
                cd.await();
            } catch (InterruptedException e) {

            }
            threadsRunningAdd(-1);

        }

    }

    private void threadsRunningAdd(int i) {
        try {
            forRunning.acquire();
            threadsRunning+=i;
            forRunning.release();
        } catch (InterruptedException e) {

        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                running.setText(RUNNING_STR+Integer.toString(threadsRunning));
            }
        });
        if(threadsRunning==0){
            stop.setEnabled(false);
            concurent.setEnabled(true);
            single.setEnabled(true);
            elapsed.setText(ELAPSED_STR+(System.currentTimeMillis()-start)+"ms");
        }

    }
}
