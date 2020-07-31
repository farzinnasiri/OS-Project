package threads.zoo;

import threads.Animal;
import threads.Cell;
import threads.GUI;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ZooController implements Runnable {

    private Zoo zoo;

    private AtomicInteger totalPopulation;
    private AtomicInteger timeUnitsPassed;
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
        timeUnitsPassed = new AtomicInteger();

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
        //cycle of life!
        while (running && zoo.isOpen()) {
            System.out.println("new cycle");
            updateScreen();
            birth();
            life();
            death();
            timeUnitsPassed.addAndGet(1);
            System.out.println("cycle ended");


        }

    }

    private void updateScreen() {
        try {
            zoo.zooMutexLock.acquire();
            zoo.zooState.set(4); // set state to displaying
            synchronized (notify) {
                notify.notifyAll();
            }
            busyWait();
            System.out.println("new screen");
            gui.refreshScreen(zoo.getTable());
            gui.updateStatus(totalPopulation.get(), timeUnitsPassed.get(), zoo.getState());
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
            busyWait();
//            System.out.println("before");
//            System.out.println(totalPopulation.get());
            System.out.println("giving birth");
            for (Cell[] cells : zoo.getTable()) {
                for (Cell cell : cells) {
                    totalPopulation.addAndGet(-cell.getAnimals().size());
                    cell.breed(zoo.getTimeUnit());
                    totalPopulation.addAndGet(cell.getAnimals().size());
                    gui.updateStatus(totalPopulation.get(), timeUnitsPassed.get(), zoo.getState());

                }
            }
//            System.out.println("after");
//            System.out.println(totalPopulation.get());

            busyWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            zoo.zooMutexLock.release();
        }
    }

    private void life() {
        try {
            zoo.zooMutexLock.acquire();
            zoo.zooState.set(2);
            synchronized (notify) {
                notify.notifyAll();
            }
            System.out.println("now living");
            synchronized (this) {
                wait(zoo.getTimeUnit() * 1000);
            }
            gui.updateStatus(totalPopulation.get(), timeUnitsPassed.get(), zoo.getState());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            zoo.zooMutexLock.release();
        }
    }

    private void death() {
        try {
            zoo.zooMutexLock.acquire();
            zoo.zooState.set(3);
            synchronized (notify) {
                notify.notifyAll();
            }
            busyWait();
            System.out.println("now killing");
            for (Cell[] cells : zoo.getTable()) {
                for (Cell cell : cells) {
                    totalPopulation.addAndGet(-cell.getAnimals().size());
                    cell.killExtraAnimals();
                    totalPopulation.addAndGet(cell.getAnimals().size());
                }
            }
            startHunt();
            gui.updateStatus(totalPopulation.get(), timeUnitsPassed.get(), zoo.getState());
            synchronized (notify) {
                notify.notifyAll();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            zoo.zooMutexLock.release();
        }
    }

    // starting the hunting process
    // death value is (number of each kind * kind value)
    private void startHunt() {
        // get cells with animals inside
        // for each cell find xy
        // for each cell get all neighbors
        // calculate xy of neighbors
        // compare cell value with neighbors value
        // if passed kill the cell!
        List<Cell> fullCells;
        if (zoo.isNote1()) {
            for (int r = 1; r < zoo.getKinds() + 1; r++) {
                fullCells = getTotalNonEmptyCells(r);
                huntCells(fullCells);
            }
        } else {
            fullCells = getTotalNonEmptyCells(-1);
            huntCells(fullCells);
        }

    }

    private void huntCells(List<Cell> fullCells) {
        for (Cell fullCell : fullCells) {
            List<Cell> neighbors = getCellNeighbors(fullCell);
            int cellDeathValue = fullCell.getCellAnimalKind() * fullCell.getAnimals().size();
            int[] deathValues = getDeathValue(neighbors); // int[r+1]
            for (int r = 1; r < deathValues.length; r++) {
                if (cellDeathValue < deathValues[r] && fullCell.getCellAnimalKind() != r) {
                    totalPopulation.addAndGet(-fullCell.getAnimals().size());
                    fullCell.killAllAnimals();
                    break;
                }
            }

        }
    }

    private int[] getDeathValue(List<Cell> neighbors) {
        int[] deathValues = new int[zoo.getKinds() + 1]; // r+1 size
        for (Cell neighbor : neighbors) {
            deathValues[neighbor.getCellAnimalKind()] += neighbor.getCellAnimalKind() * neighbor.getAnimals().size();
        }
        return deathValues;

    }

    private List<Cell> getTotalNonEmptyCells(int kind) {
        List<Cell> fullCells = new LinkedList<>();
        for (Cell[] cells : zoo.getTable()) {
            for (Cell cell : cells) {
                if (cell.getAnimals().size() > 0 && cell.getCellAnimalKind() > 0) {
                    if (kind > 0 && cell.getCellAnimalKind() == kind) fullCells.add(cell);
                    else if (kind == -1) fullCells.add(cell);
                }
            }
        }
        return fullCells;
    }


    public void busyWait() {
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
                        .moveIn(new Animal((i + 1), ((j + 1) * zoo.getNumColumns() / zoo.getNumOfEachKind()) - 1,
                                ((i + 1) * zoo.getNumRows() / zoo.getKinds()) - 1,
                                this));
            }
        }
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
        int y = cell.getY();
        int x = cell.getX();
        if (x == 0 && j < x) {
            return false;
        }
        if (x == zoo.getNumColumns() - 1 && j > x) {
            return false;
        }
        if (y == 0 && i < y) {
            return false;
        }
        if (y == zoo.getNumRows() - 1 && i > y) {
            return false;
        }
        if (zoo.isNote2()) {
            // statement 1 pass
            if (cell.getCellAnimalKind() > zoo.getKinds() / 2) {
                Cell neighbor = zoo.getTable()[i][j];
                // statement 2 pass
                if (neighbor.getCellAnimalKind() < cell.getCellAnimalKind()) {
                    if ((j == x - 1 && i == y - 1) ||
                            (j == x + 1 && i == y + 1) ||
                            (j == x + 1 && i == y - 1) ||
                            (j == x - 1 && i == y + 1)) {
                        return false;
                    }
                }

            }
        }
        return cell.getX() != j || cell.getY() != i;

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


    public Object getNotify() {
        return notify;
    }

    public int getTimeUnit() {
        return zoo.getTimeUnit();
    }

    public boolean isNote3(){
        return zoo.isNote3();
    }
}