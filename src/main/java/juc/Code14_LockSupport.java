package juc;

import java.util.concurrent.locks.LockSupport;

/**
 * 也是用来暂停线程，唤醒线程
 * @Author 张三金
 * @Date 2022/1/18 0018 21:56
 * @Company jzb
 * @Version 1.0.0
 */
public class Code14_LockSupport {

    // @TODO 这里如果子线程park() 了两次，主线程只 unpark() 一次的话会导致主线程阻塞
    public static void main(String[] args) {

        Thread t = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                System.out.println(i);
                if (i % 5 == 0) {
                    LockSupport.park();
                }
            }
        });
        t.start();
        for (int i = 0; i < 2; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (i == 0) {
                System.out.println("暂停一下！");
            }
        }
        System.out.println("接着走吧");
        LockSupport.unpark(t);
    }
}
