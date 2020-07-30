package threads;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private JLabel[][] labels;

    public GUI(int rows, int columns) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridLayout(rows, columns));

        labels = new JLabel[rows][columns];

        for (int i = 0; i < labels.length; i++) {
            for (int j = 0; j < labels[i].length; j++) {
                labels[i][j] = new JLabel();
                add(labels[i][j]);
            }
        }
        setSize(columns * 160, rows * 35);
        setLocationRelativeTo(null);
        setVisible(true);

    }

    public void refreshScreen(Cell[][] table) {
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                labels[i][j].setText(table[i][j].getAnimals().size() +
                        "animals type:" + table[i][j].getCellAnimalType());
            }
        }
        repaint();
        revalidate();

    }
}
