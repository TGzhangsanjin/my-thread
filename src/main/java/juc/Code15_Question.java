package juc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * 题目：
 *  实现一个容器，提供两个方法 add(), size()
 *  实现两个线程， 线程 1 添加10个元素到容器中，线程 2 监控容器中的个数变化，当个数到5个时，线程2给出提示并结束
 * @Author 张三金
 * @Date 2022/1/19 0019 9:26
 * @Company jzb
 * @Version 1.0.0
 */
public class Code15_Question {

    /**
     * 方式1（不行）：使用 volatile 修饰变量，线程之间可以共享，但是会不及时
     */
    public static class Test01 {
        public static class MyContainer01 {

            volatile List<Object> list = new ArrayList<>();
            public void add() {
                for (int i = 0; i < 10; i++) {
                    list.add(new Object());
                    System.out.println("count add one, current count = " + list.size());

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            public int size() {
                return list.size();
            }
        }

        public static void main(String[] args) {
            MyContainer01 container = new MyContainer01();
            Thread t1 = new Thread(() -> {
                container.add();
            });
            t1.start();

            Thread t2 = new Thread( () -> {
                while (true) {
                    if (container.size() == 5) {
                        System.out.println("Thread-2 end");
                        break;
                    }
                }
            });
            t2.start();
        }
    }

    /**
     * 方法2：线程1和线程2同时加锁，且锁是同一个的对象，线程1增加了 5个元素时，调用 wait() 方法阻塞，
     *      线程2当判断出有5个元素时，结束线程2的业务，并且唤醒线程1
     */
    public static class Test02 {

        public static class MyContainer02 {

            List<Object> list = new ArrayList<>();
            public synchronized void add() {
                for (int i = 0; i < 10; i++) {
                    if (i == 5) {
                        try {
                            // 线程阻塞，并且释放锁
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    list.add(new Object());
                    System.out.println("count add one, current count = " + list.size());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            public int size() {
                return list.size();
            }
        }
        public static void main(String[] args) {
            MyContainer02 container = new MyContainer02();
            Thread t1 = new Thread(() -> {
                container.add();
            });
            Thread t2 = new Thread( () -> {
                while (true) {
                    synchronized (container) {
                        if (container.size() == 5) {
                            System.out.println("Thread-2 end");
                            // 唤醒之前阻塞了的线程
                            container.notify();
                            break;
                        }
                    }

                }
            });
            t1.start();
            t2.start();
        }
    }
}
