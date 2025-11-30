import java.util.concurrent.locks.ReentrantLock;

public class Bank {

    private static class Account {
        public ReentrantLock lock = new ReentrantLock();
        private int balance;
        Account(int balance) {
            lock.lock();
            try{
                this.balance = balance;
            } finally {
                lock.unlock();
            }
        }
        int balance() {
            lock.lock();
            try{
                return balance;
            } finally {
                lock.unlock();
            }
        }
        boolean deposit(int value) {
            lock.lock();
            try{
                balance += value;
                return true;
            } finally {
                lock.unlock();
            }
        }
        boolean withdraw(int value) {
            lock.lock();
            try{
                if (value > balance)
                    return false;
                balance -= value;
                return true;
            } finally {
                lock.unlock();
            }
        }

    }
    private int slots;
    private Account[] av;

    public Bank(int n) {
        slots = n;
        av = new Account[slots];
        for (int i = 0; i < slots; i++)
            av[i] = new Account(0);
    }

    public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        return av[id].balance();
    }

    public boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].deposit(value);
    }

    public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].withdraw(value);
    }

    public boolean transfer(int id1, int id2, int value) {
        if (id1 < 0 || id1 >= slots || id2 < 0 || id2 >= slots)
            return false;
        if(id1 > id2){
            int temp = id1;
            id1 = id2;
            id2 = temp;
        }
        av[id1].lock.lock();
        av[id2].lock.lock();
        try{
            if (av[id1].withdraw(value)) {
                if(!av[id2].deposit(value)) {
                    av[id1].deposit(value);
                    return false;
                } else {
                    return true;
                }
            }
            return false;
        } finally {
            av[id1].lock.unlock();
            av[id2].lock.unlock();
        }
    }

    public int totalBalance() {
        for(int i = 0; i < slots; i++){
            av[i].lock.lock();
        }
        int counter = 0;
        for (int i = 0; i < slots; i++){
            counter += av[i].balance();
            av[i].lock.unlock();
        }
        return counter;
    }
}