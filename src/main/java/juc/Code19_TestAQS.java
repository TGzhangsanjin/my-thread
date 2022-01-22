package juc;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author 张三金
 * @Date 2022/1/21 0021 20:11
 * @Company jzb
 * @Version 1.0.0
 */
public class Code19_TestAQS {

    public static volatile int i = 0;

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        // 手动加锁
        lock.lock();
        try{
            System.out.println("My Business!!!!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
