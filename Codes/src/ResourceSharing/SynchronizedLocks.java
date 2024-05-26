package ResourceSharing;

public class SynchronizedLocks {
    public static void main(String[] args) throws InterruptedException {
        IncrementCounter counter = new IncrementCounter();
        IncrementingThread t1 = new IncrementingThread(counter);
        DecrementingThread t2 = new DecrementingThread(counter);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(counter.getItems());
    }

    private static class IncrementingThread extends Thread {
        private IncrementCounter incrementCounter;

        public IncrementingThread(IncrementCounter incrementCounter) {
            this.incrementCounter = incrementCounter;
        }

        @Override
        public void run() {
            for(int i=0; i<10000; i++) {
                incrementCounter.increment();
            }
        }
    }

    private static class DecrementingThread extends Thread {
        private IncrementCounter incrementCounter;

        public DecrementingThread(IncrementCounter incrementCounter) {
            this.incrementCounter = incrementCounter;
        }

        @Override
        public void run() {
            for(int i=0; i<10000; i++) {
                incrementCounter.decrement();
            }
        }
    }

    private static class IncrementCounter {
        private int items = 0;
        Object lock = new Object();
        public void increment() {
            synchronized (this.lock) {
                items++;
            }
        }

        public void decrement() {
            synchronized (this.lock) {
                items--;
            }
        }

        public int getItems() {
            return items;
        }
    }
}
