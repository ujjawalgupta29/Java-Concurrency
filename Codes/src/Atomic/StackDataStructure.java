package Atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class StackDataStructure {

    public static void main(String[] args) throws InterruptedException {
//        StandardStack<Integer> stack = new StandardStack<>();
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        Random random = new Random();

        for(int i=0; i<100000; i++) {
            stack.push(random.nextInt());
        }

        int pushingThreads = 2;
        int poppingThreads = 2;

        List<Thread> threads = new ArrayList<>();

        for(int i=0; i<pushingThreads; i++) {
            Thread t = new Thread(() -> {
                while(true) {
                    stack.push(random.nextInt());
                }
            });
            t.setDaemon(true);
            threads.add(t);
        }

        for(int i=0; i<poppingThreads; i++) {
            Thread t = new Thread(() -> {
                while(true) {
                    stack.pop();
                }
            });
            t.setDaemon(true);
            threads.add(t);
        }

        for(Thread t : threads) {
            t.start();
        }

        Thread.sleep(10000);

        System.out.println(stack.getCounter() + " opeartion performed in 10 sec");
    }

    private static class LockFreeStack<T> {
        private AtomicReference<StackNode> head = new AtomicReference<>();
        private AtomicInteger counter = new AtomicInteger(0);

        public void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            while(true) {
                StackNode<T> currentHead = head.get();
                newHead.next = currentHead;
                if(head.compareAndSet(currentHead, newHead)) {
                    break;
                }
                else {
                    LockSupport.parkNanos(1);
                }
            }
            counter.incrementAndGet();
        }

        public T pop() {
            StackNode<T> currentHead = head.get();
            StackNode<T> newHead;

            while(currentHead != null) {
                newHead = currentHead.next;
                if(head.compareAndSet(currentHead, newHead)) {
                    break;
                }
                else {
                    LockSupport.parkNanos(1);
                    currentHead = head.get();
                }
            }
            counter.incrementAndGet();
            return currentHead != null ? currentHead.value : null;
        }

        public int getCounter() {
            return counter.get();
        }
    }
    private static class StandardStack<T> {
        private StackNode<T> head;
        int counter = 0;

        public synchronized void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            newHead.next = head;
            head = newHead;
            counter++;
        }

        public synchronized T pop() {
            if(head == null) {
                counter++;
                return null;
            }

            T value = head.value;
            head = head.next;
            counter++;
            return value;
        }

        public int getCounter() {
            return counter;
        }
    }
    private static class StackNode<T> {
        public T value;
        public StackNode<T> next;

        public StackNode(T value) {
            this.value = value;
            this.next = null;
        }
    }
}
