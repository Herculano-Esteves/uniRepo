import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {
    private final int N;
    private int count = 0;
    ReentrantLock lock = new ReentrantLock();
    Condition cond = lock.newCondition();
    int generation = 0;

    public Barrier(int N) {
        this.N = N;
    }

    public synchronized void await() throws InterruptedException {
        lock.lock();
        try{
            int mygen = generation;
            count++;

            if(count == N){
                generation++;
                count = 0;
                cond.notifyAll();
                return;
            }

            while (generation == mygen) {
                cond.wait();
            }
            return;
        } finally {
            lock.unlock();
        }
    }

    class Agreement {
        private final int N;
        ReentrantLock lock = new ReentrantLock();
        Condition cond = lock.newCondition();
        int generation = 0;
        int count = 0;
        int max = 0;
        int maxTemp = 0;

        Agreement (int N) {
            this.N = N;
        }

        int propose(int choice) throws InterruptedException {
            lock.lock();
            try{
                int myGen = generation;
                count++;
                if (count == 1) {
                    max = choice;
                } else if (choice > max) {
                    max = choice;
                }
                if(N == count){
                    count = 0;
                    maxTemp = max;
                    generation++;
                    cond.signalAll();
                    return maxTemp;
                }
                while(myGen == generation){
                    cond.await();
                }
                return maxTemp;
            } finally {
                lock.unlock();
            }
        }
    }
}
