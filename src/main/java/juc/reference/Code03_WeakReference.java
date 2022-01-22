package juc.reference;

import java.lang.ref.WeakReference;

/**
 * 弱引用  --  遭遇到 GC 就会被回收
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
