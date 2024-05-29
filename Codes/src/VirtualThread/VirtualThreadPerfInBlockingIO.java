package VirtualThread;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VirtualThreadPerfInBlockingIO {
    public static int NUMBER_OF_TASKS = 10_000;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Press Enter to start");
        s.nextLine();

        System.out.println("Running " + NUMBER_OF_TASKS + " number of tasks");

        long start = System.currentTimeMillis();
        performTasks();
        System.out.println("Time taken: " + (System.currentTimeMillis() - start));
    }

    private static void performTasks() {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        try {
            for(int i=0; i<NUMBER_OF_TASKS; i++) {
                executorService.submit(() -> blockingIOOperation());
            }
        } finally {
            executorService.shutdown();
            try {
                // Wait for all tasks to complete or timeout after a certain period
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    // Wait again for shutdown
                    if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        System.err.println("ExecutorService did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void blockingIOOperation() {
        System.out.println("Executing a blocking task from thread: " + Thread.currentThread());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}

