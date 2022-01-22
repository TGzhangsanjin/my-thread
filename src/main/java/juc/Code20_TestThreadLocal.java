package juc;

/**
 * 本地线程变量
 *
 * ThreadLocal 相当于是使用空间换时间，避免了 synchronized 的使用，让数据在各个线程之间独立
 *
 * @Author 张三金
 * @Date 2022/1/22 0022 11:53
 * @Company jzb
 * @Version 1.0.0
 */
public class Code20_TestThreadLocal {

    public static void main(String[] args) {
        ThreadLocal<Person> threadLocal = new ThreadLocal<>();

        new Thread(() -> {
            threadLocal.set(new Person());
        }).start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            System.out.println(threadLocal.get());
        }).start();
    }

    public static class Person {
        String name = "zhangsan";
    }
}
