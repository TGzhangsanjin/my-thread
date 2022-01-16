package juc;

/**
 *
 *  http://www.cnblogs.com/nexiyi/p/java_memory_model_and_thread.html
 * 1. volatile 关键字，使一个变量在多个线程间可见
 *      A B 线程都用到一个变量，该变量会在共享内存（堆内存）中， java 默认是A、B线程中各自保留一份副本，这样如果B线程修改了该变量，则A线程未必知道
 *      使用 volatile 关键字，会让所有的线程都读到变量的修改值，
 *      使用 volatile 关键字，会让所有的线程都去堆内存中读值（那是不是效率就降低了？？？？）
 * 2. 使用 volatile 不能保证多个线程共同修改 running 变量时所带来的的不一致问题，也就是说 volatile 不能替代 synchronized
 *
 * 3. volatile 引用类型（包括数组）只能保证引用本本身的可见性，不能保证内部字段的可见性，具体见 Test02
 *
 * 文章链接： http://www.cnblogs.com/nexiyi/p/java_memory_model_and_thread.html
 * @Author 张三金
 * @Date 2022/1/16 0016 16:01
 * @Company jzb
 * @Version 1.0.0
 */
public class Code05_volatile {

    /**
     * volatile： 保证线程间的可见性
     */
    public static class Test01 {
        volatile boolean running = true;

        // 不加 volatile 这个方法就永远不会停止
        void m () {
            System.out.println("m start");
            while (running) {
            }
            System.out.println("m end");
        }

        public static void main(String[] args) {
            Test01 t = new Test01();

            new Thread(t::m, "thread-1").start();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            t.running = false;
        }
    }

    /**
     * volatile 引用类型（包括数组）只能保证引用本本身的可见性，不能保证内部字段的可见性
     * 这句话有一定的无解，，其实 volatileClass 这个对象中的 volatileClass.test02Running 是可见的，
     * 即可见的是一个volatileClass 这个对象
     * 即volatileClass这个对象以及他内部的所有引用对于其它线程来说都是可见的，
     * 但是 test02Running 这个变量是不可见的，即Test02.test02Running 是可不见的
     */
    public static class Test02 {

        static boolean test02Running = true;

        volatile static Test02 volatileClass = new Test02();

        void m() {
            System.out.println("m start");
            // test02Running 是不可见的， volatileClass.test02Running 是可见的
            while (test02Running) {

            }
            System.out.println("m end");
        }

        public static void main(String[] args) {
            new Thread(volatileClass::m, "Thread 1").start();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            test02Running = false;
        }
    }

    /**
     * volatile 保证不了线程安全，要保证线程安全，还是需要依赖加锁
     */
    public static class Test03 {

        volatile int count;

        // 不加锁，保证不了线程安全
        synchronized void addOne () {
            this.count++;
        }

        public static void main(String[] args) {
            Test03 t = new Test03();
            Thread[] threads = new Thread[100000];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(t::addOne, "thread-" + i);
            }
            for (int i = 0; i < threads.length; i++) {
                threads[i].start();
            }

            // 这个循环的目的是保证所以子线程都执行完了，再去执行下面的 System.out.println("count = " + t.count);
            for (int i = 0; i < threads.length; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("count = " + t.count);
        }
    }


}
