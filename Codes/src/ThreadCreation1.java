public class ThreadCreation1 {
    public static void main(String[] args) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("In thread " + Thread.currentThread().getName());
            }
        });

        System.out.println("Current thread " + Thread.currentThread().getName());
        t.setName("New Worker thread");
        t.start();

        System.out.println("Current thread " + Thread.currentThread().getName());
    }
}
