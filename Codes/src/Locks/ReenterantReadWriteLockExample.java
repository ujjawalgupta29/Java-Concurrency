package Locks;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReenterantReadWriteLockExample {

    public static final int HIGHEST_PRICE = 1000;

    public static void main(String[] args) throws InterruptedException {
        InventoryDatabase inventoryDatabase = new InventoryDatabase();

        Random random = new Random();
        for(int i=0; i<100000; i++) {
            inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
        }

        Thread writer = new Thread(() -> {
           while(true) {
               inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
               inventoryDatabase.removeItem(random.nextInt(HIGHEST_PRICE));

               try {
                   Thread.sleep(10);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        });

        writer.setDaemon(true);
        writer.start();

        int numberOfThreads = 7;
        List<Thread> readers = new ArrayList<>();

        for(int readerIndex=0; readerIndex < numberOfThreads; readerIndex++) {
            Thread reader = new Thread(() -> {
               for(int i=0; i<100000; i++) {
                   int ub = random.nextInt(HIGHEST_PRICE);
                   int lb = (ub > 0) ? random.nextInt(ub) : 0;
                   inventoryDatabase.getNumberOfItemsInRange(lb, ub);
               }
            });

            reader.setDaemon(true);
            readers.add(reader);
        }

        long startReading = System.currentTimeMillis();

        for(Thread reader : readers) {
            reader.start();
        }
        for(Thread reader : readers) {
            reader.join();
        }

        long endReading = System.currentTimeMillis();

        System.out.println("Time taken: " + (endReading - startReading));
    }
    public static class InventoryDatabase {
        private TreeMap<Integer, Integer> priceCountMap = new TreeMap<>();
        private ReentrantLock lock = new ReentrantLock();
        private ReentrantReadWriteLock reenterantReadWriteLock = new ReentrantReadWriteLock();
        private Lock readLock = reenterantReadWriteLock.readLock();
        private Lock writeLock = reenterantReadWriteLock.writeLock();
        public int getNumberOfItemsInRange(int lb, int ub) {
            readLock.lock();
            try {
                Integer fromKey = priceCountMap.ceilingKey(lb);
                Integer toKey = priceCountMap.floorKey(ub);

                if(fromKey == null || toKey == null) {
                    return 0;
                }

                NavigableMap<Integer, Integer> rangeOfPrices = priceCountMap.subMap(fromKey, true, toKey, true);

                int sum = 0;
                for(int items : rangeOfPrices.values()) {
                    sum += items;
                }

                return sum;
            }
            finally {
                readLock.unlock();
            }
        }

        public void addItem(int price) {
            writeLock.lock();
            try {
                Integer numberOfItemsForPrice = priceCountMap.get(price);
                if (numberOfItemsForPrice == null) {
                    priceCountMap.put(price, 1);
                } else {
                    priceCountMap.put(price, numberOfItemsForPrice + 1);
                }
            }
            finally {
                writeLock.unlock();
            }
        }

        public void removeItem(int price) {
            writeLock.lock();
            try {
                Integer numberOfItemsForPrice = priceCountMap.get(price);
                if (numberOfItemsForPrice == null || numberOfItemsForPrice == 1) {
                    priceCountMap.remove(price);
                } else {
                    priceCountMap.put(price, numberOfItemsForPrice - 1);
                }
            }
            finally {
                writeLock.unlock();
            }
        }
    }
}
