package juc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁, 相当于一个门有两种类型的锁
 *  1. 读锁，当线程给这个们上了一把读锁时，，其它拿着 readWriteLock.readLock() 的线程也可以进来，但是拿着 writeLock() 的线程不可进来
 *  2. 写锁，当线程给这个们上了一把写锁时，不管其它线程拿的是什么锁，当前线程不释放，其它线程都进不来
 * @Author 张三金
 * @Date 2022/1/18 0018 20:55
 * @Company jzb
 * @Version 1.0.0
 */
public class Code11_ReadWriteLock {

    static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    static Lock readLock = readWriteLock.readLock();
    static Lock writeLock = readWriteLock.writeLock();


    public static void read (Lock readLock) {
        try{
            readLock.lock();
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + "  read");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
    }

    public static void write (Lock lock) {
        try{
            lock.lock();
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + "  write");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        Runnable readRunnable = () -> read(readLock);
        Runnable writeRunnable = () -> write(writeLock);

        for (int i = 0; i < 10; i++) {
          new Thread(readRunnable, "Thread-" + i).start();
        };
        System.out.println("=============");
        for (int i = 0; i < 10; i++) {
            new Thread(writeRunnable, "Thread-" + i).start();
        }
    }


}
