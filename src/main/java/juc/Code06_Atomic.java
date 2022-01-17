package juc;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * 1. Atomic包下面的
 *      a) 线程安全的，方法本身都是原子性的
 *      b) longAdder 使用了分段锁
 *
 * 2. ABA 问题
 *      A线程将 count从1改为0， 然后又将 count 从0改为1，之后B线程再去获取count的值时，发现没有变化，以为没有其它线程处理过，
 *      解决： 给count 加一个版本号就可以解决此问题
 *
 * @Author 张三金
 * @Date 2022/1/16 0016 18:01
 * @Company jzb
 * @Version 1.0.0
 */
public class Code06_Atomic {

    // AtomicInteger
    public static class Test01 {
        AtomicInteger atom = new AtomicInteger(0);
        int unsafe;

        void m () {
            for (int i = 0; i < 1000; i++) {
                atom.incrementAndGet();
                unsafe++;
            }
        }

        public static void main(String[] args) {
            Test01 t = new Test01();
            Thread[] threads = new Thread[1000];
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

            System.out.println(t.atom.get());
            System.out.println(t.unsafe);
        }
    }

    // LongAdder
    public static class Test02 {
        public LongAdder longAdder = new LongAdder();

        void m () {
            for (int i = 0; i < 1000; i++) {
                longAdder.increment();
            }
        }

        public static void main(String[] args) {
            Test02 t = new Test02();
            Thread[] threads = new Thread[1000];
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

            System.out.println(t.longAdder.longValue());
        }

    }

    /**
     * AtomicInteger VS LongAdder VS synchronized
     *
     *  很尴尬： AtomicInteger 和 LongAdder 的优势我没能测出来，可能是因为其本身的优势就在于线程少，线程执行时间短的情况，所以不明显
     *          而 synchronized 的优势很明显测出来了，只有线程一多，单个线程的执行时间变成，立马 synchronized 的执行效率就上去了
     */
    public static class Test03 {
        AtomicInteger atom = new AtomicInteger(0);
        LongAdder longAdder = new LongAdder();
        int count;

        int length = 100000;

        void m1 () {
            for (int i = 0; i < length; i++) {
                atom.incrementAndGet();
            }
        }
        void m2 () {
            for (int i = 0; i < length; i++) {
                longAdder.increment();
            }
        }
        synchronized void m3 () {
            for (int i = 0; i < length; i++) {
                count++;
            }
        }

        public static void main(String[] args) {
            Test03 t = new Test03();
            Thread[] threads = new Thread[100];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(t::m1);
            }
            long start = System.currentTimeMillis();
            System.out.println("AtomicInteger start");
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
            long end = System.currentTimeMillis();
            System.out.println(t.atom.get() + " AtomicInteger 耗时： " + (end - start) + " ms");

            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(t::m2);
            }
            start = System.currentTimeMillis();
            System.out.println("LongAdder start");
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
            end = System.currentTimeMillis();
            System.out.println(t.longAdder.longValue() + " LongAdder 耗时： " + (end - start) + " ms");


            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(t::m3);
            }
            start = System.currentTimeMillis();
            System.out.println("LongAdder start");
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
            end = System.currentTimeMillis();
            System.out.println(t.count + " synchronized 耗时： " + (end - start) + " ms");
        }
    }
}
