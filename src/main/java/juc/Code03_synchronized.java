package juc;

/**
 * 锁
 * @Author 张三金
 * @Date 2022/1/13 0013 21:31
 * @Company jzb
 * @Version 1.0.0
 */
public class Code03_synchronized implements Runnable{

    public int count = 100;

    // 1. 锁住方法，这时候这把锁是当前类
    public synchronized void run () {
        count--;
        System.out.println(Thread.currentThread().getName() + ": count=" + count);
    }

    public synchroni

    public static void main(String[] args) {
        Code03_synchronized t = new Code03_synchronized();
        for (int i = 0; i < 100; i++) {
            new Thread(t, "Thread-" + i).start();
        }
    }
}
