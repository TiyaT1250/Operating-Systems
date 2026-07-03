import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OSProject2 {
    public static void main(String[] args) {
        // Task 1: Simulate threads from processes.txt 
        new ProcessThread(101, 2).start();
        new ProcessThread(102, 3).start();

        // Task 2: Dining Philosophers [cite: 32, 33]
        int n = 5;
        Lock[] forks = new ReentrantLock[n];
        for (int i = 0; i < n; i++) forks[i] = new ReentrantLock();

        for (int i = 0; i < n; i++) {
            // Deadlock avoidance: pick up lower-indexed fork first 
            Lock f1 = forks[i];
            Lock f2 = forks[(i + 1) % n];
            if (i == n - 1) new Philosopher(i, f2, f1).start(); 
            else new Philosopher(i, f1, f2).start();
        }
    }
}

class ProcessThread extends Thread {
    int pid, burst;
    ProcessThread(int p, int b) { pid = p; burst = b; }
    public void run() {
        System.out.println("Process " + pid + " started.");
        try { Thread.sleep(burst * 1000); } catch (Exception e) {} 
        System.out.println("Process " + pid + " finished."); 
    }
}

class Philosopher extends Thread {
    int id; Lock first, second;
    Philosopher(int id, Lock f1, Lock f2) { this.id = id; first = f1; second = f2; }
    public void run() {
        try {
            System.out.println("[Philo " + id + "] Waiting for forks..."); 
            first.lock(); second.lock();
            System.out.println("[Philo " + id + "] Eating..."); 
            Thread.sleep(1000);
            first.unlock(); second.unlock();
            System.out.println("[Philo " + id + "] Released forks."); 
        } catch (Exception e) {}
    }
}
