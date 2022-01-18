package juc;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 栅栏，累积多少个线程之后一起执行
 * @Author 张三金
 * @Date 2022/1/18 0018 19:59
 * @Company jzb
 * @Version 1.0.0
 */
public class Code09_CyclicBarrier {

    public static void main(String[] args) {
        // 集齐10个线程，再一起执行
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10, () -> {
            System.out.println("有10个线程来了，发车");
        });

        Thread[] threads = new Thread[20];
        for (int i = 0; i < threads.length; i++) {
            new Thread(() -> {
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "涌出来了");
            }).start();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        try {
            cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
