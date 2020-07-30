package threads.zoo;

import java.util.concurrent.atomic.AtomicInteger;

public class ZooController implements Runnable {

    private Zoo zoo;

    private AtomicInteger totalPopulation;

    private AtomicInteger totalMovingAnimals;
    private AtomicInteger totalWaitingAnimals;

    private Thread zooController;
    private boolean running;

    public ZooController(Zoo zoo) {
        this.zoo = zoo;

        totalMovingAnimals = new AtomicInteger();
        totalWaitingAnimals = new AtomicInteger();


        totalPopulation = new AtomicInteger();
        totalPopulation.set(zoo.getKinds() * zoo.getNumOfEachKind()); // set total initial population

        zooController = new Thread(this);
    }

    public void start() {
        zoo.setOpen(true);
        running = true;
        zooController.start();
    }

    @Override
    public void run() {

    }
}
