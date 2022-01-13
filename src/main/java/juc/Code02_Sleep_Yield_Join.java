package juc;

import org.junit.Test;

/**
 * 三个线程的方法
 *
 * 1. sleep()
 * 2. yield() 当前线程让出CPU，让其他线程来竞争,,其实并没有完全让步，只是当前线程占有CPU的时候，我稍微停一下，给其它线程一个机会，咋们再来公平竞争
 * 3. join() 在 T1 线程中 调用个 T2.join(),, 表示T1线程暂停，等T2线程结束后，T1线程再接着往下执行
 *
 * ps: 对于第3点，留一个坑，，如果 T1和T2 互相 join 会是一种什么情况
 *
 * @Author 张三金
 * @Date 2022/1/13 0013 20:44
 * @Company jzb
 * @Version 1.0.0
 */
public class Code02_Sleep_Yield_Join {

    public static void main(String[] args) {
//        testSleep();
//        testYield();
        testJoin();
    }

    public static void testSleep() {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                    System.out.println("test sleep()");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void testYield () {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                if (i % 10 == 0) {
                    Thread.yield();
                }
                System.out.println("t1");
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println("t2");
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });

        t1.start();
        t2.start();
    }

    public static void testJoin () {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("t1");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("t2");
                if (i == 2) {
                    try {
                        // 一直到 t1线程结束，t2 线程再接着往下执行
                        t1.join();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t1.start();
        t2.start();
    }
}
