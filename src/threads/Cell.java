package threads;

import threads.zoo.ZooController;

import javax.swing.*;
import java.util.LinkedList;

public class Cell {

    // position of cell in the zoo table
    private final int x, y;

    // every cell can contain only of kind of animal (r). if a cell is empty, any animal can enter
    private int cellAnimalType;
    private int timePassed;
    private LinkedList<Animal> animals;

    int maxCapacity; // k

    private ZooController zooController;

    public Cell(int x, int y, int maxCapacity, ZooController zooController) {
        this.x = x;
        this.y = y;
        this.maxCapacity = maxCapacity;
        this.zooController = zooController;
        animals = new LinkedList<>();
    }

    // to start all the animal threads that are initial position in the cell
    public void start() {
        for (Animal animal : animals) {
            animal.start();
        }
    }

    // to end all the animal threads that are currently inside this cell
    public void end() {
        cellAnimalType = 0;
        for (Animal animal : animals) {
            animal.end();
        }
        animals.clear();

    }

    // move an animal to the cell
    public synchronized boolean moveIn(Animal animal) {
        // if no animal is in this cell, it can enter
        if (cellAnimalType == 0) {
            timePassed = 0;
            cellAnimalType = animal.getType();
        }
        // animal with different type from the cell cant enter
        if (animal.getType() == cellAnimalType) {
            if (maxCapacity > cellAnimalType * (animals.size() + 1)) {
                animals.add(animal);
                return true;
            }
        }
        return false;
    }

    // move out an animal form the cell
    public synchronized boolean moveOut(Animal animal) {
        if (animal.getType() == cellAnimalType) {
            if (animals.size() - 1 > 0) {
                animals.remove(animal);
                return true;
            } else if (animals.size() - 1 == 0) {
                animals.remove(animal);
                cellAnimalType = 0;
                timePassed = 0;
                return true;
            }
        }
        return false;
    }

    public synchronized void breed() {
        if (cellAnimalType == 0) {
            return;
        }
        // for every cell, every `i` time units breeding happens
        // this method is called every 1 time unit
        timePassed += 1;
        if (timePassed == cellAnimalType) {
            timePassed = 0;
            Animal newAnimal;
            for (int i = 0; i < animals.size(); i++) {
                newAnimal = new Animal(cellAnimalType, x, y, zooController);
                newAnimal.start();
                animals.add(newAnimal);
            }
        }

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
