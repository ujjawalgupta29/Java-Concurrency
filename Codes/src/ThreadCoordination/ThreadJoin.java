package ThreadCoordination;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadJoin {

    public static void main(String[] args) throws InterruptedException {
        List<FactorialThread> threads = new ArrayList<>();
        List<Long> nums = Arrays.asList(0L, 32L, 987L,489L, 1000000L);

        for(long num : nums) {
            threads.add(new FactorialThread(num));
        }

        for(Thread thread : threads) {
//            thread.setDaemon(true);
            thread.start();
        }

        for(Thread thread : threads) {
            thread.join(2000);
        }

        for(int i=0; i<nums.size(); i++) {
            FactorialThread t = threads.get(i);
            if(t.isFinished()) {
                System.out.println("Factorial of " + nums.get(i) + " is: " + t.getResult());
            }
            else {
                System.out.println("Factorial of " + nums.get(i) + " is in progress");
            }
        }

        for(FactorialThread thread : threads) {
            if(!thread.isFinished())
                thread.interrupt();
        }
    }
    private static class FactorialThread extends Thread{
        private long inputNumber;
        private BigInteger result = BigInteger.ONE;
        private boolean isFinished = false;

        public FactorialThread(long num) {
            inputNumber = num;
        }

        @Override
        public void run() {
            this.result = factorial(inputNumber);
            this.isFinished = true;
        }

        private BigInteger factorial(long num) {
            BigInteger temp = BigInteger.ONE;
            for(long i=num; i>0; i--) {
                if(this.isInterrupted()) {
                    return new BigInteger("-1");
                }
                temp = temp.multiply(new BigInteger(Long.toString(i)));
            }
            return temp;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public BigInteger getResult() {
            return result;
        }
    }
}
