package juc.reference;

import java.lang.ref.WeakReference;

/**
 * 弱引用  --  遭遇到 GC 就会被回收
 * 一个弱引用的对象如果同时被一个强引用所指向，当这个强引用消失的时候，这个弱应用就应该被gc回收，
 * 弱引用 在 ThreadLocal 的 Entry 对象是这样的
 *
 * @TODO ThreadLocal 中的一张图需要画一下
 * @Author 张三金
 * @Date 2022/1/22 0022 15:02
 * @Company jzb
 * @Version 1.0.0
 */
public class Code03_WeakReference {

    public static void main(String[] args) {

        WeakReference<M> m = new WeakReference<>(new M());

        System.out.println("对象还在： " + m.get());

        System.gc();

        System.out.println("对象被 gc 回收了 ： " + m.get());

    }
}
