package threads.zoo;

import threads.Animal;
import threads.Cell;
import threads.GUI;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ZooController implements Runnable {

    private Zoo zoo;

    private AtomicInteger totalPopulation;

    private AtomicInteger totalMovingAnimals;
    private AtomicInteger totalWaitingAnimals;

    private Object notify;

    private Thread zooController;
    private boolean running;

    private GUI gui;

    public ZooController(Zoo zoo, GUI gui) {
        this.zoo = zoo;
        this.gui = gui;

        totalMovingAnimals = new AtomicInteger();
        totalWaitingAnimals = new AtomicInteger();

        totalPopulation = new AtomicInteger();
        totalPopulation.set(zoo.getKinds() * zoo.getNumOfEachKind());
        // set total initial population

        notify = new Object();

        zoo.setTable(setUpTable());
        initializeAnimals();

        zooController = new Thread(this);
    }

    public void start() {
        zoo.setOpen(true);
        running = true;
        zooController.start();

        for (Cell[] cells : zoo.getTable()) {
            for (Cell cell : cells) {
                cell.start();
            }
        }

    }

    public void end() {
        zoo.setOpen(false);
        running = false;
    }

    @Override
    public void run() {
        while (running && zoo.isOpen()) {
            updateScreen();
            birth();

        }

    }

    private void updateScreen() {
        try {
            zoo.zooMutexLock.acquire();
            zoo.zooState.set(4); // set state to displaying
            synchronized (notify) {
                notify.notifyAll();
            }
            waiting();
            gui.refreshScreen(zoo.getTable());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            zoo.zooMutexLock.release();
            synchronized (notify) {
                notify.notifyAll();
            }
        }

    }

    private void birth() {
        try {
            zoo.zooMutexLock.acquire();
            zoo.zooState.set(1); // set state to displaying
            synchronized (notify) {
                notify.notifyAll();
            }
            waiting();

            for (Cell[] cells : zoo.getTable()) {
                for (Cell cell : cells) {
                    totalPopulation.addAndGet(-cell.getAnimals().size());
                    cell.breed();
                    totalPopulation.addAndGet(cell.getAnimals().size());
                }
            }


            waiting();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            zoo.zooMutexLock.release();
        }
    }


    public void waiting() {
        while (totalMovingAnimals.get() > 0 ||
                totalPopulation.get() != totalWaitingAnimals.get()) {
            synchronized (this) {
                try {
                    wait(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Cell[][] setUpTable() {
        Cell[][] table = new Cell[zoo.getNumRows()][zoo.getNumColumns()];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[0].length; j++) {
                table[i][j] = new Cell(j, i, zoo.getMaxCellCapacity(), this);
            }
        }
        return table;
    }

    private void initializeAnimals() {
        for (int i = 0; i < zoo.getKinds(); i++) {
            for (int j = 0; j < zoo.getNumOfEachKind(); j++) {
                zoo.getTable()
                        [((i + 1) * zoo.getNumRows() / zoo.getKinds()) - 1]
                        [((j + 1) * zoo.getNumColumns() / zoo.getNumOfEachKind()) - 1]
                        .moveIn(new Animal((i + 1), ((i + 1) * zoo.getNumRows() / zoo.getKinds()) - 1,
                                ((j + 1) * zoo.getNumColumns() / zoo.getNumOfEachKind()) - 1,
                                this));
            }
        }
    }


    public int getZooState() {
        return zoo.getState();
    }

    public AtomicInteger getTotalMovingAnimals() {
        return totalMovingAnimals;
    }

    public AtomicInteger getTotalWaitingAnimals() {
        return totalWaitingAnimals;
    }

    public Cell[][] getZooTable() {
        return zoo.getTable();
    }

    public ArrayList<Cell> getCellNeighbors(Cell cell) {
        ArrayList<Cell> neighbors = new ArrayList<>();
        for (int i = cell.getY() - 1; i <= cell.getY() + 1; i++) {
            for (int j = cell.getX() - 1; j <= cell.getX() + 1; j++) {
                if (isCellValidNeighbor(i, j, cell)) {
                    neighbors.add(zoo.getTable()[i][j]);
                }
            }
        }
        return neighbors;

    }

    private boolean isCellValidNeighbor(int i, int j, Cell cell) {
        if (cell.getX() == 0 && j < cell.getX()) {
            return false;
        }
        if (cell.getX() == zoo.getNumColumns() - 1 && j > cell.getX()) {
            return false;
        }
        if (cell.getY() == 0 && j < cell.getY()) {
            return false;
        }
        if (cell.getY() == zoo.getNumRows() - 1 && j > cell.getY()) {
            return false;
        }
        return cell.getX() != j || cell.getY() != i;

    }

    public Object getNotify() {
        return notify;
    }
}