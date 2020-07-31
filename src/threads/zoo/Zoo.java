package threads.zoo;

import threads.Cell;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Zoo {
    // initial values
    private int r;
    private int n;
    private int m;
    private int s;
    private int k;
    private int t;

    // notes! (تصبره ها)
    private boolean note1;
    private boolean note2;
    private boolean note3;


    // The zoo table for keeping the animals
    private Cell[][] Table;

    /*
     zoo state, representing that the zoo is in which state:
     state = 1, birth(beginning of time unit)
     state = 2, life(during the time unit)
     state = 3, death(end of time unit)
     state = 4, displaying the table
    */
    AtomicInteger zooState;

    // binary semaphore(mutex) to lock the zoo!
    Semaphore zooMutexLock;

    // is zoo open or not
    private boolean open;


    public Zoo(int r, int n, int m, int s, int k, int t, boolean note1, boolean note2, boolean note3) {
        this.r = r;
        this.n = n;
        this.m = m;
        this.s = s;
        this.k = k;
        this.t = t;
        this.note1 = note1;
        this.note2 = note2;
        this.note3 = note3;

        // this binary semaphore will guarantee first-in first-out granting of permits under contention
        zooMutexLock = new Semaphore(1, true);

        zooState = new AtomicInteger(1); // ready to give birth!

    }


    public Cell[][] getTable() {
        return Table;
    }

    public void setTable(Cell[][] table) {
        Table = table;
    }

    public int getState() {
        return zooState.get();
    }

    public boolean setState(int newState) {
        if (newState > 4 || newState < 1) {
            return false;
        }
        zooState.set(newState);
        return true;
    }


    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public int getKinds() {
        return r;
    }

    public int getNumRows() {
        return n;
    }

    public int getNumColumns() {
        return m;
    }

    public int getNumOfEachKind() {
        return s;
    }

    public int getMaxCellCapacity() {
        return k;
    }

    public int getTimeUnit() {
        return t;
    }


    public boolean isNote1() {
        return note1;
    }

    public boolean isNote2() {
        return note2;
    }

    public boolean isNote3() {
        return note3;
    }
}
