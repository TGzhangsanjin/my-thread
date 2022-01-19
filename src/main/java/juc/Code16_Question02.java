package juc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 写一个固定容量同步容器，拥有 put 和 get 方法，以及 getCount 方法
 * 能够支持2个生产者线程和10个消费者线程
 * @Author 张三金
 * @Date 2022/1/19 0019 20:12
 * @Company jzb
 * @Version 1.0.0
 */
public class Code16_Question02 {

    /**
     * 使用 wait() 和 notifyAll() 实现
     */
    public static class Test01 {
        public static class MyContainer01 {

            int count = 100;

            public synchronized void put () {
                // 这里为什么要用 while 而不用 if？？？
                // 原因是因为，被唤醒的时候，这个手的 count依旧是100，那么就继续阻塞， 如果是 if 的话，就会导致继续往下执行，这个时候 count 就会超过 100 了
                while (count == 100) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.notifyAll();
                count++;
                System.out.println("count put, count = " + count);
            }

            public synchronized void get () {
                while (count == 0) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count--;
                this.notifyAll();
                System.out.println("count get, count = " + count);
            }
        }

        public static void main(String[] args) {
            MyContainer01 container = new MyContainer01();
            Thread[] producer = new Thread[20];
            Thread[] consumer = new Thread[100];
            for (int i = 0; i < producer.length; i++) {
                producer[i] = new Thread(() -> {
                    container.put();
                });
            }
            for (int i = 0; i < consumer.length; i++) {
                consumer[i] = new Thread(() -> {
                    container.get();
                });
            }
            for (int i = 0; i < producer.length; i++) {
                producer[i].start();
            }
            for (int i = 0; i < consumer.length; i++) {
                consumer[i].start();
            }
        }
    }



    /**
     * 使用 Lock 和 Condition 实现, 这样唤醒的时候就只唤醒指定类型的线程
     */
    public static class Test02 {
        public static class MyContainer01 {

            Lock lock = new ReentrantLock();
            Condition producer = lock.newCondition();
            Condition consumer = lock.newCondition();

            int count = 100;

            public void put () {
                try {
                    lock.lock();
                    while (count == 100) {
                        producer.await();
                    }
                    count++;
                    System.out.println("count put, count = " + count);
                    // 数量 +1了，通知消费者可以去消费了
                    consumer.signalAll();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }

            }

            public synchronized void get () {
                try {
                    lock.lock();
                    while (count == 0) {
                        consumer.wait();
                    }
                    count--;
                    System.out.println("count get, count = " + count);
                    // 数量 -1了， 通知生产者可以去生产了
                    producer.signalAll();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }

        public static void main(String[] args) {
            MyContainer01 container = new MyContainer01();
            Thread[] producer = new Thread[20];
            Thread[] consumer = new Thread[100];
            for (int i = 0; i < producer.length; i++) {
                producer[i] = new Thread(() -> {
                    container.put();
                });
            }
            for (int i = 0; i < consumer.length; i++) {
                consumer[i] = new Thread(() -> {
                    container.get();
                });
            }
            for (int i = 0; i < producer.length; i++) {
                producer[i].start();
            }
            for (int i = 0; i < consumer.length; i++) {
                consumer[i].start();
            }
        }
    }




}
