class Increment implements Runnable {

    public void run() {
        final long I=100;
        for (long i = 0; i < I; i++)
            System.out.println(i);
    }
}

class main {
    public static void main(String[] a) {
        int N = 10;
        Thread[] threads = new Thread[N];
        for(int i = 0; i < N; i++){
            threads[i] = new Thread(new Increment());
            threads[i].start();
        }
        for(Thread t : threads){
            try{
                t.join();
            } catch (InterruptedException e){
                System.out.println(e);
            }
        }
        System.out.println("fim");
    }
}
