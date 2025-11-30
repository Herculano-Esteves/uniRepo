import java.util.concurrent.locks.ReentrantLock;

public class Bank2 {

    private static class Account {
        private int balance;
        Account(int balance) { this.balance = balance; }
        int balance() { return balance; }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    private int slots;
    private Account[] av;
    private ReentrantLock lock = new ReentrantLock();

    public Bank2(int n) {
        slots = n;
        av = new Account[slots];
        for (int i = 0; i < slots; i++)
            av[i] = new Account(0);
    }

    public int balance(int id) {
        lock.lock();
        try {
            if (id < 0 || id >= slots)
                return 0;
            return av[id].balance();
        } finally {
            lock.unlock();
        }
    }

    public boolean deposit(int id, int value) {
        lock.lock();
        try {
            if (id < 0 || id >= slots)
                return false;
            return av[id].deposit(value);
        } finally {
            lock.unlock();
        }
    }

    public boolean withdraw(int id, int value) {
        lock.lock();
        try {
            if (id < 0 || id >= slots)
                return false;
            return av[id].withdraw(value);
        } finally {
            lock.unlock();
        }
    }

    public boolean transfer(int id1, int id2, int value) {
        lock.lock();
        try {
            // ✅ Corrigido: verificar ambos os índices
            if (id1 < 0 || id1 >= slots || id2 < 0 || id2 >= slots)
                return false;
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
            lock.unlock();
        }
    }

    public int totalBalance() {
        lock.lock();
        try {
            int counter = 0;
            for (int i = 0; i < slots; i++)
                counter += av[i].balance();
            return counter;
        } finally {
            lock.unlock();
        }
    }
}
