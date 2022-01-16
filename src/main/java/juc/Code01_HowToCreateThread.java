package juc;

/**
 * 创建线程的三种方式  启动线程都需要使用 Thread 类的 start() 方法
 * 1. 继承 Thread 类
 * 2. 实现 Runnable  接口 （最终启动线程时，任就需要 new Thread()）
 * 3. 使用 lambda 或者 线程池的方式
 *
 */
public class Code01_HowToCreateThread {

    static class MyThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("it is my thread!");
        }
    }

    static class MyRun implements Runnable {

        @Override
        public void run() {
            System.out.println("it is my run!");
        }
    }

    public static void main(String[] args) {
        // 1. 方式1
        new MyThread().start();
        // 2. 方式2 （终究是需要 new Thread() ）
        new Thread(new MyRun()).start();
        // 3. lambda 表达式的方式
        new Thread(() -> {
            System.out.println("Lambda");
        }).start();
    }
}
