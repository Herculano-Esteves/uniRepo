import java.util.Random;

public class BankTest {

    private static class Worker implements Runnable {
        private Bank b;
        private int accs;
        private int iters;

        public Worker(Bank b, int accs, int iters) {
            this.b = b;
            this.accs = accs;
            this.iters = iters;
        }

        public void run() {
            Random rand = new Random();
            for (int i = 0; i < iters; i++) {
                int op = rand.nextInt(3);
                int id1 = rand.nextInt(accs + 5) - 2;  // pode gerar índices inválidos
                int id2 = rand.nextInt(accs + 5) - 2;
                int val = 10;

                switch (op) {
                    case 2 -> b.transfer(id1, id2, val);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int ACCS = 10;
        int ITERS = 100000;
        Bank b = new Bank(ACCS);
        for (int i = 0; i < ACCS; i++)
            b.deposit(i, 1000);

        int balance1 = b.totalBalance();
        System.out.println("Initial total: " + balance1);

        Thread[] threads = new Thread[8];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Worker(b, ACCS, ITERS));
            threads[i].start();
        }
        for (Thread t : threads) t.join();

        int balance2 = b.totalBalance();
        System.out.println("Final total:   " + balance2);

        if (balance1 != balance2)
            System.out.println("❌ Inconsistency detected!");
        else
            System.out.println("✅ All good");
    }
}
