package threads;

public class Animal implements Runnable {
    private int type; // animal type (r)

    // animals current position
    private int x, y;

    private Thread animal;
    private boolean alive;


    public Animal(int type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
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

        }
    }

    public int getType() {
        return type;
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
