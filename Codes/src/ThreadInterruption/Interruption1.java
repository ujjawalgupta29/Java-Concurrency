package ThreadInterruption;

public class Interruption1 {

    public static void main(String[] args) {
        Thread t = new Thread(new BlockingTask());
        t.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        t.interrupt();
    }
    private static class BlockingTask implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                System.out.println("Exiting Blocking Thread");
            }
        }
    }
}
