package juc.reference;

/**
 * @Author 张三金
 * @Date 2022/1/22 0022 14:13
 * @Company jzb
 * @Version 1.0.0
 */
public class M {


    /**
     * 对象被回收之后会调用这个方法，这个方法是没有人会去重写的，这里只是为了看看效果
     */
    @Override
    protected void finalize() throws Throwable {
        System.out.println("回收了！");
        super.finalize();
    }
}
