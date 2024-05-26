package ResourceSharing;

import java.util.Random;

public class MetricsExample {

    public static void main(String[] args) {
        Metrics metrics = new Metrics();
        MetricsPrinter metricsPrinter = new MetricsPrinter(metrics);
        BusinessLogic businessLogic1 = new BusinessLogic(metrics);
        BusinessLogic businessLogic2 = new BusinessLogic(metrics);

        businessLogic1.start();
        businessLogic2.start();
        metricsPrinter.start();
    }

    public static class MetricsPrinter extends Thread {
        private Metrics metrics;

        public MetricsPrinter(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                double currentAvg = metrics.getAvg();

                System.out.println("Curr Avg is " + currentAvg);
            }
        }
    }

    public static class BusinessLogic extends Thread {
        private Metrics metrics;
        private Random random = new Random();

        public BusinessLogic(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {

            while(true) {
                long start = System.currentTimeMillis();

                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                long end = System.currentTimeMillis();

                metrics.addSample(end - start);
            }

        }
    }
    public static class Metrics {
        private long counts = 0;
        private volatile double avg = 0.0;

        public synchronized void addSample(long sample) {
            double currSum = avg * counts;
            counts++;
            avg = (currSum + sample) /counts;
        }

        public double getAvg() {
            return avg;
        }
    }
}
