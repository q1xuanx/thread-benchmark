package com.test_algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    CountDownLatch latch;
    private int[] numbers;

    public Main(){
        this.numbers = new int[100_000];
    }

    Runnable task = new Runnable() {
        public void run() {
            try {
                int[] copyOfNumbers = Arrays.copyOf(numbers, numbers.length);
                List<Integer> arrayList = new ArrayList<>();
                for (int i = 0; i < copyOfNumbers.length; i++) {
                    if (copyOfNumbers[i] % 2 == 0) {
                        arrayList.add(copyOfNumbers[i]);
                    }
                }
            } finally {
                latch.countDown();
            }
        }
    };

    private void threadBenchMark() throws InterruptedException {
        int numberOfThreads = 1_000_000;
        latch = new CountDownLatch(numberOfThreads);
        generateNumbers(numbers);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(task).start();
        }
        latch.await(); // wait all thread have completed their task
        long endTime = System.currentTimeMillis();
        System.out.println("Total time regular thread execution: " + (endTime - startTime) + "ms");

        startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfThreads; i++) {
            Thread.startVirtualThread(task);
        }
        latch.await();
        endTime = System.currentTimeMillis();
        System.out.println("Total time virtual thread execution: " + (endTime - startTime) + "ms");
    }

    private int[] generateNumbers(int [] numbers) {
        Random random = new Random();
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = random.nextInt(1_000_001);
        }
        return numbers;
    }

    public static void main(String[] args) throws InterruptedException {
        Main main = new Main();
        main.threadBenchMark();
    }
}