package juc;

public class Code01_HowToCreateThread {

    static class MyThread extends Thread {
        @Override
        public void run() {
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
        new MyThread().start();
        new MyThread(new MyRun()).start();
    }
}
