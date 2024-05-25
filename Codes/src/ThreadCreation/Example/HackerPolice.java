package ThreadCreation.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HackerPolice {

    public static int maxPassword = 9999;

    public static void main(String[] args) {
        Random random = new Random();
        Vault vault = new Vault(random.nextInt(maxPassword));
        List<Thread> threads = new ArrayList<>();
        threads.add(new AscendingHacker(vault));
        threads.add(new DescendingHacker(vault));
        threads.add(new Police());

        for(Thread t : threads) {
            t.start();
        }
    }

    private static class Vault {
        private int password;

        public Vault(int password) {
            this.password = password;
        }

        public boolean guessPassword(int pwd) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return pwd == password;
        }
    }
    private static abstract class Hacker extends Thread {
        protected Vault vault;

        public Hacker(Vault vault) {
            this.vault = vault;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start() {
            System.out.println("Starting thread " + this.getName());
            super.start();
        }
    }

    private static class AscendingHacker extends Hacker {

        public AscendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for(int guess = 0; guess <= maxPassword; guess++) {
                if(vault.guessPassword(guess)) {
                    System.out.println(this.getName() + " guessed the password: " + guess);
                    System.exit(0);
                }
            }
        }
    }

    private static class DescendingHacker extends Hacker {

        public DescendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for(int guess = maxPassword; guess >= 0; guess--) {
                if(vault.guessPassword(guess)) {
                    System.out.println(this.getName() + " guessed the password: " + guess);
                    System.exit(0);
                }
            }
        }
    }

    private static class Police extends Thread {
        @Override
        public void run() {
            for(int i=10; i>0; i--) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Seconds left: " + i);
            }
            System.out.println("Hacker got caught");
            System.exit(0);
        }
    }
}
