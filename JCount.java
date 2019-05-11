import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class JCount extends JPanel {
    private String DEFAULT_VALUE = "1000000";
    private int WRITING_INTERVAL = 10_000;
    private int SLEEP_DURATION = 100;
    private int DISTANCE_BETWEEN_OBJECTS = 100;
    private JButton start;
    private JButton stop;
    private JTextField number;
    private JLabel label;
    private Worker counterGuy;

    //every JCount object is in one line because its better this way
    public JCount() {
        super();
        start = new JButton("Start");
        stop = new JButton("Stop");
        number = new JTextField(DEFAULT_VALUE, 10);
        label = new JLabel("0");
        addActionListeners(number, start, stop);

        add(number);
        add(label);
        add(start);
        add(stop);
        //add some right space so that everything is nice when label gets bigger
        add(Box.createRigidArea(new Dimension(DISTANCE_BETWEEN_OBJECTS, 0)));
    }

    private void addActionListeners(JTextField number, JButton start, JButton stop) {
        number.addActionListener(actionEvent -> {
            if (counterGuy == null) {
                counterGuy = new Worker(Integer.parseInt(number.getText()));
            } else {
                counterGuy.interrupt();
                counterGuy = new Worker(Integer.parseInt(number.getText()));
            }
            counterGuy.start();
        });

        stop.addActionListener(actionEvent -> {
            if (counterGuy != null)
                counterGuy.interrupt();
            counterGuy = null;
        });
        start.addActionListener(actionEvent -> {
            if (counterGuy == null) {
                counterGuy = new Worker(Integer.parseInt(number.getText()));
            } else {
                counterGuy.interrupt();
                counterGuy = new Worker(Integer.parseInt(number.getText()));
            }
            counterGuy.start();
        });
    }


    private class Worker extends Thread {
        private int countTo;

        public Worker(int i) {
            this.countTo = i;
        }

        @Override
        public void run() {
            int counter = 0;
            while (counter <= countTo) {
                counter++;
                if (isInterrupted()) break;
                if (counter % WRITING_INTERVAL == 0) {
                    try {
                        Thread.sleep(SLEEP_DURATION);
                    } catch (InterruptedException e) {
                        break;
                    }
                    int finalCounter = counter;
                    SwingUtilities.invokeLater(() -> label.setText(String.valueOf(finalCounter)));

                }

            }
        }
    }


    private static void createAndShowGUI() {
        lookAndFeel();
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (int i = 0; i < 4; i++) {
            panel.add(new JCount());
            panel.add(Box.createRigidArea(new Dimension(0, 40)));
        }
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void lookAndFeel() {
        try {

            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }


}

