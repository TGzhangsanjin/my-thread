package juc.reference;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.ref.SoftReference;

/**
 * 软引用
 * 软引用是用来描述一些还有用，但非必须的对象 （和缓存的含义很相似）
 * 对于软引用关联着的对象，在内存即将发生内存溢出之际，将会把这些对象放进回收范围进行二次回收
 * 如果这次回收之后还没有足够的内存，则会爆出内存溢出
 *
 *
 * @Author 张三金
 * @Date 2022/1/22 0022 14:36
 * @Company jzb
 * @Version 1.0.0
 */
public class Code02_SoftReference {

    public static void main(String[] args) {
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        System.out.println("最大可用内存:" + (mbean.getHeapMemoryUsage().getMax() / 1024 / 1024) + "M");

        SoftReference<byte[]> soft = new SoftReference<>(new byte[1024 * 1024 * 10]);
        System.gc(); // 这个时候调用gc是不会回收 soft，因为内存还没有满
        System.out.println(soft.get());

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] b = new byte[1024 * 1024 * 10];
        System.gc();
        System.out.println("soft 被回收了，" + soft.get());
        System.out.println("b 还在 " + b);
    }
}
