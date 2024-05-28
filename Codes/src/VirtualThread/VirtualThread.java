package VirtualThread;

import java.util.ArrayList;
import java.util.List;

public class VirtualThread {

    public static int NUMBER_OF_VIRTUAL_THREADS = 100;
    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> System.out.println("Inside thread: " + Thread.currentThread());

        List<Thread> threads = new ArrayList<>();

        for(int i=0; i<NUMBER_OF_VIRTUAL_THREADS; i++) {
            Thread thread = Thread.ofVirtual().unstarted(runnable);
            threads.add(thread);
        }

        for(Thread thread : threads) {
            thread.start();
        }

        for(Thread thread : threads) {
            thread.join();
        }
    }
}
