package juc;

/**
 * synchronized 加锁的几种方式
 * 1. 锁住方法
 * 2. synchronized(this) 与第1钟情况一样，都是使用 当前类作为锁
 * 3. synchronized(object) 指定一个对象作为锁
 * 4. synchronized 锁的可重入性
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

}
