import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

class Bank {

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

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;
    ReentrantLock bankLock = new ReentrantLock();

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance);
        bankLock.lock();
        try{
            int id = nextId;
            nextId += 1;
            map.put(id, c);
            return id;
        } finally {
            bankLock.unlock();
        }
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        bankLock.lock();
        Account c = null;
        try{
            c = map.remove(id);
        } finally {
            bankLock.unlock();
        }
        if (c == null) {
            return 0;
        }
        return c.balance();
    }

    // account balance; 0 if no such account
    public int balance(int id) {
        bankLock.lock();
        Account c = null;
        try{
            c = map.get(id);
        } finally {
            bankLock.unlock();
        }
        if (c == null)
            return 0;
        return c.balance();
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        bankLock.lock();
        Account c = null;
        try{
            c = map.get(id);
        } finally {
            bankLock.unlock();
        }
        if (c == null)
            return false;
        return c.deposit(value);
    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        bankLock.lock();
        Account c = null;
        try{
            c = map.get(id);

        } finally {
            bankLock.unlock();
        }
        if (c == null)
            return false;
        return c.withdraw(value);
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        Account cfrom = null, cto = null;cto = null;
        if(from > to){
            int temp = from;
            from = to;
            to = temp;
        }
        bankLock.lock();
        try{
            cfrom = map.get(from);
            cto = map.get(to);
        } finally {
            bankLock.unlock();
        }
        if (cfrom == null || cto ==  null) {
            return false;
        }
        cfrom.lock.lock();
        try{
            cto.lock.lock();
            try{
                return cfrom.withdraw(value) && cto.deposit(value);
            } finally {
                cto.lock.unlock();
            }
        } finally {
            cfrom.lock.unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist
        public int totalBalance(int[] ids) {
            ArrayList<Account> accountsToProcess = new ArrayList<Account>();
            bankLock.lock();
            try {
                Arrays.sort(ids);
                for (int id : ids) {
                    Account c = map.get(id);
                    if (c == null) {
                        return 0;
                    }
                    accountsToProcess.add(c);
                }
            } finally {
                bankLock.unlock();
            }
            for(Account c : accountsToProcess){
                c.lock.lock();
            }
            int total = 0;
            try {
                for (Account acct : accountsToProcess) {
                    total += acct.balance();
                }
                return total;
            } finally {
                for (int i = accountsToProcess.size() - 1; i >= 0; i--) {
                    accountsToProcess.get(i).lock.unlock();
                }
            }
        }

}
