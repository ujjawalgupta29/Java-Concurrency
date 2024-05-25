package ThreadCreation;

public class ThreadCreation2 {
    public static void main(String[] args) {
        Thread t = new NewThread();
        t.setName("Test Thread");
        t.start();
        System.out.println("In thread " + Thread.currentThread().getName());

    }

    private static class NewThread extends Thread {
        @Override
        public void run() {
            System.out.println("In thread " + Thread.currentThread().getName());
        }
    }
}
