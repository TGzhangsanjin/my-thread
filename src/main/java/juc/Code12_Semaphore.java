package juc;

import java.util.concurrent.Semaphore;

/**
 * 信号量，允许多少个线程同时执行（感觉有点儿像线程池）
 * @Author 张三金
 * @Date 2022/1/18 0018 21:13
 * @Company jzb
 * @Version 1.0.0
 */
public class Code12_Semaphore {


    public static class MyThread implements Runnable {

        String name;

        Semaphore s;

        public MyThread (String name, Semaphore s) {
            this.name = name;
            this.s = s;
        }

        @Override
        public void run() {
            try{
                s.acquire();
                System.out.println(name);
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                s.release();
            }
        }
    }



    public static void main(String[] args) {

        // 允许两个线程同时执行
        Semaphore s = new Semaphore(2);

        Thread[] threads = new Thread[10];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new MyThread("Thread-" + i, s));
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }


    }
}
