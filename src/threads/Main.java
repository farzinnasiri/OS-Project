package threads;

import threads.zoo.Zoo;
import threads.zoo.ZooController;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        // creating input pane
        JPanel panel = new JPanel(new GridLayout(9, 3));

        JLabel label1 = new JLabel("r:");
        JTextField text1 = new JTextField("3", 10);

        JLabel label2 = new JLabel("n:");
        JTextField text2 = new JTextField("7", 10);

        JLabel label3 = new JLabel("m:");
        JTextField text3 = new JTextField("6", 10);

        JLabel label4 = new JLabel("s:");
        JTextField text4 = new JTextField("2", 10);

        JLabel label5 = new JLabel("k:");
        JTextField text5 = new JTextField("10", 10);

        JLabel label6 = new JLabel("t:");
        JTextField text6 = new JTextField("1", 10);

        JLabel label7 = new JLabel("note1:");
        JCheckBox checkBox1 = new JCheckBox();

        JLabel label8 = new JLabel("note2:");
        JCheckBox checkBox2 = new JCheckBox();

        JLabel label9 = new JLabel("note3:");
        JCheckBox checkBox3 = new JCheckBox();

        panel.add(label1);
        panel.add(text1);
        panel.add(label2);
        panel.add(text2);
        panel.add(label3);
        panel.add(text3);
        panel.add(label4);
        panel.add(text4);
        panel.add(label5);
        panel.add(text5);
        panel.add(label6);
        panel.add(text6);
        panel.add(label7);
        panel.add(checkBox1);
        panel.add(label8);
        panel.add(checkBox2);
        panel.add(label9);
        panel.add(checkBox3);


        int result = JOptionPane.showConfirmDialog(null, panel, "Please Enter the following values",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int r = Integer.parseInt(text1.getText()); // number of kinds
            int n = Integer.parseInt(text2.getText()); // number of rows
            int m = Integer.parseInt(text3.getText()); // number of columns
            int s = Integer.parseInt(text4.getText()); // number of each kind
            int k = Integer.parseInt(text5.getText()); // maximum number of each
            int t = Integer.parseInt(text6.getText()); // time unit
            boolean note1 = checkBox1.isSelected(); // note 1 (تبصره ۱)
            boolean note2 = checkBox2.isSelected();// note 2 ( تبصره ۲)
            boolean note3 = checkBox3.isSelected(); // // note 3: animal of kind i should die after i units of time!

            Zoo zoo = new Zoo(r, n, m, s, k, t, note1, note2, note3);
            GUI gui = new GUI(n, m);
            ZooController zooController = new ZooController(zoo, gui);

            zooController.start();
        } else {
            System.exit(0);
        }


    }

}


