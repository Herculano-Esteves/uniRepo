import java.util.concurrent.locks.ReentrantLock;

class Bank implements Runnable {

    private int I; // número de depósitos por thread
    private int V; // valor de cada depósito

    ReentrantLock lock = new ReentrantLock();

    public Bank(int I, int V) {
        this.I = I;
        this.V = V;
    }

    public void run() {
        for (int i = 0; i < I; i++) {
            deposit(V);
        }
    }

    private static class Account {
        private int balance;
        Account(int balance) { this.balance = balance; }
        int balance() { return balance; }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
    }

    // Our single account, for now
    private Account savings = new Account(0);

    // Account balance
    public int balance() {
        lock.lock();
        try {
            return savings.balance();
        } finally {
            lock.unlock();
        }
    }

    // Deposit
    boolean deposit(int value) {
        lock.lock();
        try {
            return savings.deposit(value);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] s) {
        int N = 10;
        int I = 1000000;
        int V = 10;

        System.out.println("Expected balance: " + (I * V * N));

        Bank bank = new Bank(I, V);
        Thread[] threads = new Thread[N];

        // ⏱ start timer
        long startTime = System.nanoTime();

        // start threads
        for (int i = 0; i < N; i++) {
            threads[i] = new Thread(bank);
            threads[i].start();
        }

        // wait for all threads to finish
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        // ⏱ stop timer
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double seconds = duration / 1_000_000_000.0;

        System.out.println("Fim: " + bank.balance());
        System.out.printf("Execution time: %.6f seconds%n", seconds);
    }
}
