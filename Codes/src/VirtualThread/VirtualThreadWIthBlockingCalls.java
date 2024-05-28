package VirtualThread;

import java.util.ArrayList;
import java.util.List;

public class VirtualThreadWIthBlockingCalls {
    public static int NUMBER_OF_VIRTUAL_THREADS = 100;
    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for(int i=0; i<NUMBER_OF_VIRTUAL_THREADS; i++) {
            Thread thread = Thread.ofVirtual().unstarted(new BlockingTask());
            threads.add(thread);
        }

        for(Thread thread : threads) {
            thread.start();
        }

        for(Thread thread : threads) {
            thread.join();
        }
    }

    private static class BlockingTask implements Runnable {
        @Override
        public void run() {
            System.out.println("Inside thread: " + Thread.currentThread() + " before blocking call");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            System.out.println("Inside thread: " + Thread.currentThread() + " after blocking call");
        }
    }
}
