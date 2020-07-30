package threads;

import threads.zoo.ZooController;

import java.util.ArrayList;
import java.util.Random;

public class Animal implements Runnable {
    private int kind; // animal type (r)

    // animals current position
    private int x, y;

    private Thread animal;
    private boolean alive;

    private ZooController zooController;

    // random object for finding the chance of moving an animal
    private Random random;


    public Animal(int kind, int x, int y, ZooController zooController) {
        this.kind = kind;
        this.x = x;
        this.y = y;


        this.zooController = zooController;

        this.random = new Random();

        animal = new Thread(this);
    }

    public void start() {
        alive = true;
        animal.start();
    }

    public void end() {
        alive = false;
    }

    @Override
    public void run() {
        while (alive) {
            int state = zooController.getZooState();
            if (state == 2) {
                move();
            } else {
                waiting();
            }
        }
    }

    private void waiting() {
        try {
            zooController.getTotalWaitingAnimals().addAndGet(1);
            synchronized (zooController.getNotify()) {
                zooController.getNotify().wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            zooController.getTotalWaitingAnimals().addAndGet(-1);
        }

    }

    private void move() {
        zooController.getTotalWaitingAnimals().addAndGet(1);
        float chanceOfMoving = random.nextFloat();
        if (chanceOfMoving > 0.5) {
            ArrayList<Cell> neighbors = zooController.
                    getCellNeighbors(zooController.
                            getZooTable()[y][x]);
            int ran = random.nextInt(neighbors.size());
            int newX = neighbors.get(ran).getX();
            int newY = neighbors.get(ran).getY();
            if (zooController.getZooTable()[newY][newX].moveIn(this)) {
                zooController.getZooTable()[y][x].moveOut(this);
                y = newY;
                x = newX;
            }
        }
        zooController.getTotalWaitingAnimals().addAndGet(-1);
    }

    public int getKind() {
        return kind;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
