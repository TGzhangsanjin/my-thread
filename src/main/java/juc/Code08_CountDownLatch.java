package juc;

import java.util.concurrent.CountDownLatch;

/**
 *  门闩
 *
 *  线程A在某个点调用 await() 方法， 等 countDownLatch 里的值变成 0 时，则返回，否则就一直挂在这里
 *
 *  和 join 的区别，，join的话是等待某个线程执行完，这 CountDownLatch 可以设置等待指定数量的线程执行完
 *
 * @Author 张三金
 * @Date 2022/1/18 0018 19:25
 * @Company jzb
 * @Version 1.0.0
 */
public class Code08_CountDownLatch {

    public static class Test02 {
        public static void main(String[] args) {
            Thread[] threads = new Thread[100];
            // 如果 latchLength == 100, 那么主线程会等所有线程执行完再执行
            // 如果 latchLength < 100, 那么主线程可能会比部分子线程先执行完毕
            // 如果 latchLength > 100, 那么主线程就永远等在那里了
            int latchLength = 100;
            CountDownLatch latch = new CountDownLatch(latchLength);
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    System.out.println(Thread.currentThread().getName() + "");
                    // latch 中的计数减 1
                    latch.countDown();
                });
            }
            for (int i = 0; i < threads.length; i++) {
                threads[i].start();
            }
            try {
                // 当前线程在这儿等着，，等什么时候 CountDownLatch 中的数字变成0了，继续执行当前线程
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 5; i++) {
                System.out.println(i);
            }
        }
    }
}
