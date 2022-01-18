package juc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** ReentrantLock 可重入锁 （synchronized 也是可重入的）
 *
 * 1. 必须手动的释放锁 lock.unlock(), 所以一般 unlock() 都需要写在 finally 里面， 而 synchronized 如果抛出异常会自动释放锁
 * 2. tryLock()
 *      可以尝试进行获取锁，如果在指定时间内获取不到锁，可以根据具体业务的情况来处理后续（不阻塞）
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

    /**
     * tryLock()
     */
    public static class Test02 {

        Lock lock = new ReentrantLock();

        void m1 () {
            boolean locked = false;
            try {
                locked = lock.tryLock(1, TimeUnit.SECONDS);
                System.out.println("m1 locked " + lock);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (locked) {
//                    lock.unlock();
                }
            }
        }

        void m2 () {
            try {
                lock.tryLock();
                System.out.println("m2 locked " + lock);
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                lock.unlock();
                System.out.println("m2 unlocked");
            }
        }

        public static void main(String[] args) {
            Test02 t = new Test02();
            new Thread(t::m2).start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread(t::m1).start();
        }
    }

}
