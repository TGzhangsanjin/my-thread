package juc;

import java.util.concurrent.locks.ReentrantLock;

/** ReentrantLock 可重入锁 （synchronized 也是可重入的）
 *
 * 1. 必须手动的释放锁 lock.unlock(), 所以一般 unlock() 都需要写在 finally 里面， 而 synchronized 如果抛出异常会自动释放锁
 * @Author 张三金
 * @Date 2022/1/17 0017 21:00
 * @Company jzb
 * @Version 1.0.0
 */
public class Code07_ReentrantLock {

    public static class Test01 {

        int count = 0;
        ReentrantLock lock = new ReentrantLock();
        void m() {
            lock.lock();
            try{
                for (int i = 0; i < 1000; i++) {
                    count++;
                }
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
        public static void main(String[] args) {
            Test01 t = new Test01();
            Thread[] threads = new Thread[10000];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(t::m, "thread-" + i);
            }
            for (int i = 0; i < threads.length; i++) {
                threads[i].start();
            }
            for (int i = 0; i < threads.length; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(t.count);
        }
    }

    public static class Test02 {

    }

}
