package juc.reference;

import java.io.IOException;

/**
 * 强引用，也就是正常的 new 一个对象
 * @Author 张三金
 * @Date 2022/1/22 0022 14:17
 * @Company jzb
 * @Version 1.0.0
 */
public class Code01NormalReference {

    public static void main(String[] args) throws IOException {
        M m = new M();
        m = null; // 对象没用了, 才会被回收
        System.gc(); // 手动调用 gc

        System.in.read();
    }
}
