import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Warehouse2 {
    private Map<String, Product> map =  new HashMap<String, Product>();
    public ReentrantLock lock = new ReentrantLock();

    private class Product {
        Condition cond = lock.newCondition();
        int quantity = 0;
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) {
        lock.lock();
        Product p = get(item);
        try{
            p.quantity += quantity;
            p.cond.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void consume(Set<String> items) throws InterruptedException {
        lock.lock();
        try{
            boolean done = false;
            while(!done){
                done = true;
                for(String s : items){
                    Product p = get(s);
                    if(p.quantity < 1){
                        p.cond.await();
                        done = false;
                    }
                }
            }
            for (String s : items){
                Product p = get(s);
                p.quantity--;
            }
        } finally {
            lock.unlock();
        }
    }

}
