package threads;

import java.util.LinkedList;

public class Cell {

    // position of cell in the zoo table
    private final int x, y;

    // every cell can contain only of kind of animal (r). if a cell is empty, any animal can enter
    private int cellAnimalType;
    private LinkedList<Animal> animals;

    int maxCapacity; // k!

    public Cell(int x, int y, int maxCapacity) {
        this.x = x;
        this.y = y;
        this.maxCapacity = maxCapacity;
        animals = new LinkedList<>();
    }

    public void start() {
        for (Animal animal : animals) {
            animal.start();
        }
    }

    public void end() {
        cellAnimalType = 0;
        for (Animal animal : animals) {
            animal.end();
        }
        animals.clear();

    }

    public synchronized boolean moveIn(Animal animal) {
        // if no animal is in this cell
        if (cellAnimalType == 0) {
            cellAnimalType = animal.getType();
        }
        if (animal.getType() == cellAnimalType) {
            if (maxCapacity > cellAnimalType * (animals.size() + 1)) {
                animals.add(animal);
                return true;
            }
        }
        return false;
    }

    public synchronized boolean moveOut(Animal animal) {
        if (animal.getType() == cellAnimalType) {
            if (animals.size() - 1 > 0) {
                animals.remove(animal);
                return true;
            } else if (animals.size() - 1 == 0) {
                animals.remove(animal);
                cellAnimalType = 0;
                return true;
            }
        }
        return false;
    }

    public int getCellAnimalType() {
        return cellAnimalType;
    }

    public LinkedList<Animal> getAnimals() {
        return animals;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
