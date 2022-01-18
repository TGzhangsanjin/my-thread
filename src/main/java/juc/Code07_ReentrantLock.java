package juc;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** ReentrantLock 可重入锁 （synchronized 也是可重入的）
 *
 * 1. 必须手动的释放锁 lock.unlock(), 所以一般 unlock() 都需要写在 finally 里面， 而 synchronized 如果抛出异常会自动释放锁
 *
 * 2. 几种显示获取锁的方法
 *   a) tryLock()
 *       可以尝试进行获取锁，如果在指定时间内获取不到锁，可以根据具体业务的情况来处理后续（不阻塞,自然就可能导致线程不安全）
 *   b) lockInterruptibly()
 *       拿到了锁，则直接走，拿不到锁，则一直等着，当被人interrupt了，则抛出异常
 * 3. 支持公平锁和非公平锁
 *      非公平锁，当前线程执行完之后会直接去CAS，而公平锁当前线程执行完之后则是放到队列中，和其它线程公平竞争
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

        /**
         * 这里一开始一直不明白，为什么 m1没拿到所也能往下执行，是自己思路错了
         */
        void m1 () {
            boolean locked = false;
            try {
                // 如果1s内拿不到锁，会接着往下执行，这样就可能会导致这段代码时线程不安全的
                locked = lock.tryLock(1, TimeUnit.SECONDS);
                System.out.println("m1 locked " + locked);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (locked) {
                    lock.unlock();
                }
            }
        }

        void m2 () {
            boolean locked = false;
            try {
                locked = lock.tryLock();
                System.out.println("m2 " + locked);
                Thread.sleep(4000);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.println("m2 释放锁");
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

    /**
     * lockInterruptibly() 方法
     */
    public static class Test03 {
        static Lock lock = new ReentrantLock();
        public static void main(String[] args) {
            Thread t1 = new Thread(() -> {
                try{
                    lock.lock();
                    System.out.println("t1");
                    Thread.sleep(Integer.MAX_VALUE);
                    System.out.println("t1 end");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
            t1.start();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Thread t2 = new Thread(() -> {
                try{
                    // 一直等着获取锁，拿到了，正常往下走
                    // 如果在等锁的过程中，被其它线程打断了，则抛出异常
                    lock.lockInterruptibly();
                    System.out.println("t2");
                } catch (InterruptedException e) {
                    System.out.println("t2 interupted!");
                } finally {
//                    lock.unlock();
                }
            });
            t2.start();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            t2.interrupt();
        }
    }

    /**
     * 公平锁和非公平锁
     */
    public static class Test04 {
        // 公平锁，分布的更均匀一些，我这个实验没有看出啥区别
//        Lock lock = new ReentrantLock(true);
        Lock lock = new ReentrantLock();

        void m1 () {
            try {
                lock.lock();
                System.out.println("m1");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        void m2 () {
            try {
                lock.lock();
                System.out.println("m2");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public static void main(String[] args) {
            Test04 t = new Test04();
            Thread[] threads = new Thread[100];
            for (int i = 0; i < threads.length; i++) {
                if (i % 2 == 0) {
                    threads[i] = new Thread(t::m1);
                } else {
                    threads[i] = new Thread(t::m2);
                }
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

        }
    }

}
