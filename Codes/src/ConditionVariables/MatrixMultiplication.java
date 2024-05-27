package ConditionVariables;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class MatrixMultiplication {

    private static final int N = 10;
    private static final String INPUT_PATH = "./resources/matrices";
    private static final String OUTPUT_PATH = "./resources/matrices_result.txt";
    public static void main(String[] args) throws IOException {
        ThreadSafeQueue queue = new ThreadSafeQueue();
        File inputFile = new File(INPUT_PATH);
        File outputFile = new File(OUTPUT_PATH);

        MatricesReaderProducer matricesReaderProducer = new MatricesReaderProducer(new FileReader(inputFile), queue);
        MatricesMultiplierConsumer matricesMultiplierConsumer = new MatricesMultiplierConsumer(queue, new FileWriter(outputFile));

        matricesReaderProducer.start();
        matricesMultiplierConsumer.start();
    }

    public static class MatricesMultiplierConsumer extends Thread {
        private ThreadSafeQueue queue;
        private FileWriter fileWriter;

        public MatricesMultiplierConsumer(ThreadSafeQueue queue, FileWriter fileWriter) {
            this.queue = queue;
            this.fileWriter = fileWriter;
        }

        @Override
        public void run() {
            while(true) {
                MatricesPair matricesPair = queue.remove();

                if(matricesPair == null) {
                    System.out.println("No more matrices for mult, consumer is terminating");;
                    break;
                }

                float [][] result = multiplyMatrices(matricesPair.mat1, matricesPair.mat2);

                try {
                    saveMatrixToFile(fileWriter, result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                fileWriter.flush();;
                fileWriter.close();
            } catch (IOException e) {

            }
        }

        private void saveMatrixToFile(FileWriter fileWriter, float[][] matrix) throws IOException {
            for(int r=0; r<N; r++) {
                StringJoiner stringJoiner = new StringJoiner(",");
                for(int c=0; c<N; c++) {
                    stringJoiner.add(String.format("%.2f", matrix[r][c]));
                }
                fileWriter.write(stringJoiner.toString());
                fileWriter.write("\n");
            }
        }

        private float[][] multiplyMatrices(float[][] mat1, float[][] mat2) {
            float[][] result = new float[N][N];
            for(int r=0; r<N; r++) {
                for(int c=0; c<N; c++) {
                    for(int k=0; k<N; k++) {
                        result[r][c] += mat1[r][k] * mat2[k][c];
                    }
                }
            }

            return result;
        }
    }

    public static class MatricesReaderProducer extends Thread {
        private Scanner scanner;
        private ThreadSafeQueue queue;

        public MatricesReaderProducer(FileReader reader, ThreadSafeQueue queue) {
            this.scanner = new Scanner(reader);
            this.queue = queue;
        }

        @Override
        public void run() {
            while(true) {
                float[][] mat1 = readMatric();
                float[][] mat2 = readMatric();

                if(mat1 == null || mat2 == null) {
                    queue.terminate();
                    System.out.println("No more matrices. Produce is terimnating");
                    return;
                }

                MatricesPair matricesPair = new MatricesPair();
                matricesPair.mat1 = mat1;
                matricesPair.mat2 = mat2;

                queue.add(matricesPair);
            }
        }

        private float[][] readMatric() {
            float [][] matrix = new float[N][N];
            for(int r=0; r<N; r++) {
                if(!scanner.hasNext()) {
                    return null;
                }
                String [] line = scanner.nextLine().split(",");
                for(int c=0; c<N; c++) {
                    matrix[r][c] = Float.valueOf(line[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }
    }

    public static class ThreadSafeQueue {
        private Queue<MatricesPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminate = false;

        private final int capacity = 5;

        public synchronized void add(MatricesPair matricesPair) {
            while(queue.size() == capacity) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
            queue.add(matricesPair);
            isEmpty = false;
            notify();
        }

        public synchronized MatricesPair remove() {
            MatricesPair matricesPair = null;
            while(isEmpty && !isTerminate) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }

            if(queue.size() == 1) {
                isEmpty = true;
            }

            if(queue.size() == 0 && isTerminate) {
                return null;
            }

            System.out.println("queue size " + queue.size());
            matricesPair = queue.remove();
            if(queue.size() == capacity - 1) {
                notifyAll();
            }
            return matricesPair;
        }

        public synchronized void terminate() {
            isTerminate = true;
            notifyAll();
        }
    }

    private static class MatricesPair {
        public float[][] mat1;
        public float[][] mat2;
    }
}
