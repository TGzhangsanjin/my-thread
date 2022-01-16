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
 * 文章链接： http://www.cnblogs.com/nexiyi/p/java_memory_model_and_thread.html
 * @Author 张三金
 * @Date 2022/1/16 0016 16:01
 * @Company jzb
 * @Version 1.0.0
 */
public class Code05_volatile {


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
}
