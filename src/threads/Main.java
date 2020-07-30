package threads;

import threads.zoo.Zoo;
import threads.zoo.ZooController;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        int r = 3; // number of kinds
        int n = 7; // number of rows
        int m = 6; // number of columns
        int s = 2; // number of each kind
        int k = 10; // maximum number of each
        int t = 1; // time unit
        boolean note1 = true;
        boolean note2 = true;

        Zoo zoo = new Zoo(r, n, m, s, k, t, note1, note2);
        GUI gui = new GUI(n, m);
        ZooController zooController = new ZooController(zoo, gui);

        zooController.start();

    }

}


