package juc;

import java.util.Random;

/**
 * 1.  synchronized 加锁的几种方式
 *      a) 锁住方法
 *      b) synchronized(this) 与第1钟情况一样，都是使用 当前类作为锁
 *      c) synchronized(object) 指定一个对象作为锁
 *      d) synchronized 锁的可重入性
 *
 * 2. synchronized 的锁升级
 *      a) jdk早起的时候，synchronized 都是重量级锁，即都向OS申请系统锁，
 *      b) 锁升级
 *           Ⅰ.（偏向锁）当只有一个线程的时候，这个时候就是偏向锁，object 锁会标记这个线程ID
 *           Ⅱ.（自旋锁）当超过一个线程的时候，升级为自旋锁，不停的打转，看锁是否被释放，能否占用，
 *           Ⅲ.（重量级锁、系统锁）当自旋了10次以后，升级为重量级锁，直接找OS进行申请系统锁
 * 3. 锁的优化
 *      a) 锁的细化，对于单个锁来说，锁住的代码越少效率越高
 *      b) 锁的粗化，如果有很多段代码分别都加了锁，那不如考虑将这些代码合并成一个锁
 *
 * 4. 某对象o作为一把锁，如果对象o的属性发生改变，不影响锁的使用，但是若果 o 变成另外一个对象，则会影响，则锁的对象会发生改变
 *    应该避免将锁的对象引用变成另外的对象
 *
 *    ps: 对于加锁的代码
 *         如果执行时间段、线程数少，则采用自旋
 *         如果执行时间长，线程数多，则使用系统锁
 * @Author 张三金
 * @Date 2022/1/13 0013 21:31
 * @Company jzb
 * @Version 1.0.0
 */
public class Code03_synchronized implements Runnable{

    public int count = 1000;

    // 1. 锁住方法，这时候这把锁是当前类
//    public synchronized void run () {
//        count--;
//        System.out.println(Thread.currentThread().getName() + ": count=" + count);
//    }

    // 2. 锁住代码块，指定锁
//    public void run () {
//        synchronized (this) {
//            count--;
//            System.out.println(Thread.currentThread().getName() + ": count=" + count);
//        }
//    }

    // 3. 指定锁对象
    public final Object o = new Object();
    public void run () {
        synchronized (o) {
            count--;
            System.out.println(Thread.currentThread().getName() + ": count=" + count);
        }
    }

    public static void main(String[] args) {
        Code03_synchronized t = new Code03_synchronized();
        for (int i = 0; i < 1000; i++) {
            new Thread(t, "Thread-" + i).start();
        }

        try {
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName() + ": count=" + t.count);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 锁的可重入性， m1()和m2() 方法都加了了锁，锁都是同一个对象，在 m1() 中调用 m2() 的方法是允许的
     * 即一个线程拿到了一把锁，那么使用这把锁锁住的所有内容，该线程都可以访问（打开）
     */
    public static class Test01 implements Runnable{

        public static void main(String[] args) {
            Test01 test01 = new Test01();
            new Thread(test01).start();
        }


        public int count = 100;

        @Override
        public void run() {
            m1();
        }

        public void m1 () {
            synchronized (this) {
                count--;
                m2();
                System.out.println("m1 count == " + count);
            }
        }

        public void m2 () {
            synchronized (this) {
                System.out.println("m2 count == " + count);
                count--;
            }
        }
    }


    /**
     * 同步和非同步的方法是可以同时调用的
     */
    public static class Test02 {

        public synchronized void m1 () {
            System.out.println(Thread.currentThread().getName() + " m1 start");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " m1 end");
        }

        public void m2 () {
            System.out.println(Thread.currentThread().getName() + " m2 start");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " m2 end");
        }

        public static void main(String[] args) {
            Test02 t = new Test02();
            new Thread(t::m1, "t1").start();
            new Thread(t::m2, "t2").start();
        }
    }

    /**
     * 对于加锁的代码如果抛出异常了，默认情况下锁是会被释放的
     */
    public static class Test03 {
        int index = 0;
        public synchronized void m1 () {
            System.out.println(Thread.currentThread().getName() + " start");
            while (true) {
                index ++;
                System.out.println(Thread.currentThread().getName() + " index = " + index);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (index % 10 == 0) {
                    // 当前线程如果抛出异常了，默认情况下就会释放当前占用的这把锁
                    System.out.println(1/0);
                }
            }
        }

        public static void main(String[] args) {
            Test03 t = new Test03();
            new Thread(t::m1, "thread-01").start();
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread(t::m1, "thread-02").start();
        }
    }

    /**
     * 锁的优化，细化
     * 方法m2 是要比 方法 m1 的效率更高的，可以使线程争用的时间更短
     */
    public static class Test04 {
        int count;

        public synchronized void m1 () {

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            count++;

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void m2 () {

            // 这里类似于不需要加锁的业务代码
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 需要加锁的代码
            synchronized (this) {
                count++;
            }
            // 这里类似于不需要加锁的业务代码
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 某对象o作为一把锁，如果对象o的属性发生改变，不影响锁的使用，但是若果 o 变成另外一个对象，则会影响，则锁的对象会发生改变
     * 应该避免将锁的对象引用变成另外的对象
     */
    public static class Test05 {

        int count;

        static Code03_synchronized myLock = new Code03_synchronized();

        void m () {
            synchronized (myLock) {
                for (int i = 0; i < 1000; i++) {
                    count++;
                }
            }
        }

        public static void main(String[] args) {
            Test05 t = new Test05();
            Thread[] threads = new Thread[10000];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(t::m, "thread-" + i);
            }
            for (int i = 0; i < threads.length; i++) {
//                    myLock.count = (int)(Math.random() * 100);
                // 锁对象引用了一个新的对象，
                // 在修改引用前，A线程拿到了锁，进入了代码块运行
                // 在修改引用后，如果A线程还未执行完，B这个是后是可以拿到这把新锁的，这个时候A、B就会同时执行这段代码块了
                myLock = new Code03_synchronized();
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

}
