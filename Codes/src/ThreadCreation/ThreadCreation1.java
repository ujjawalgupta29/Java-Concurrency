package ThreadCreation;

public class ThreadCreation1 {
    public static void main(String[] args) {

        System.out.println(Runtime.getRuntime().availableProcessors());
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("In thread " + Thread.currentThread().getName());
            }
        });

        System.out.println("Current thread " + Thread.currentThread().getName());
        t.setName("New Worker thread");
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();

        System.out.println("Current thread " + Thread.currentThread().getName());



        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Intentional Exception");
            }
        });

        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("Error occured " + t2.getName() + " " + e.getMessage());
            }
        });
        t2.start();
    }
}
