package threads;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.jar.JarEntry;

public class GUI extends JFrame {
    private JLabel[][] labels;
    private JLabel totalPopulation;
    private JLabel state;
    private JLabel timePassed;

    public GUI(int rows, int columns) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("ZOO SIMULATOR");
        setLayout(new GridLayout(rows, columns));

        labels = new JLabel[rows][columns];

        for (int i = 0; i < labels.length; i++) {
            for (int j = 0; j < labels[i].length; j++) {
                labels[i][j] = new JLabel();
                add(labels[i][j]);
            }
        }


        setSize(columns * 160, rows * 45);
        setLocationRelativeTo(null);
        setVisible(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                openStatusWindow();

            }
        });
        thread.start();

    }

    private void openStatusWindow() {
        JPanel jPanel = new JPanel(new GridLayout(8, 3));
        totalPopulation = new JLabel("Total Population: " + 0);
        state = new JLabel("Starting");
        timePassed = new JLabel("Time units passed:" + 0);
        jPanel.add(totalPopulation);
        jPanel.add(timePassed);
        jPanel.add(state);
        JOptionPane.showMessageDialog(this, jPanel, "Simulation Status", JOptionPane.OK_OPTION);

    }

    public void refreshScreen(Cell[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                String oldValue = labels[i][j].getText();
                String newValue = table[i][j].getAnimals().size() +
                        " animals of type: " + table[i][j].getCellAnimalKind();
                if (!oldValue.equals(newValue)) {
                    labels[i][j].setForeground(Color.BLUE);
                } else {
                    labels[i][j].setForeground(Color.BLACK);
                }
                labels[i][j].setText(newValue);
            }
        }
        repaint();
        revalidate();
    }

    public void updateStatus(int population, int timePassed, int state) {
        this.totalPopulation.setText("Total Population: " + population);
        this.timePassed.setText("Time units passed: " + timePassed);
        switch (state) {
            case 1:
                this.state.setText("Birth");
                this.state.setForeground(Color.GREEN);
                break;
            case 2:
                this.state.setText("Life");
                this.state.setForeground(Color.BLUE);
                break;
            case 3:
                this.state.setText("Death");
                this.state.setForeground(Color.red);
                break;
            case 4:
                this.state.setText("Display");
                this.state.setForeground(Color.magenta);
                break;
        }
    }
}
