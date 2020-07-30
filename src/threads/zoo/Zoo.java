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

    // The zoo table for keeping the animals
    private Cell[][] Table;

    /*
     zoo state, representing that the zoo is in which state:
     state = 1, birth(beginning of time unit)
     state = 2, life(during the time unit)
     state = 3, death(end of time unit)
     state = 4, drawing( zoo is closed!)
    */
    AtomicInteger zooState;

    // binary semaphore(mutex) to lock the zoo!
    Semaphore zooMutexLock;

    private boolean open;

    public Zoo(int r, int n, int m, int s, int k, int t, boolean note1, boolean note2) {
        this.r = r;
        this.n = n;
        this.m = m;
        this.s = s;
        this.k = k;
        this.t = t;
        this.note1 = note1;
        this.note2 = note2;

        // this binary semaphore will guarantee first-in first-out granting of permits under contention
        zooMutexLock = new Semaphore(1, true);

        zooState = new AtomicInteger(1); // ready to give birth!

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
}
