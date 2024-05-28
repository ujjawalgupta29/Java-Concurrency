package VirtualThread;

public class PlatformThread {
    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> System.out.println("Inside thread: " + Thread.currentThread());

//        Thread thread = new Thread(runnable);
//         OR
        Thread thread = Thread.ofPlatform().unstarted(runnable);
        thread.start();
        thread.join();
    }
}
