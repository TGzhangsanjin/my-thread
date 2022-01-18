package juc;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 两个线程之间进行交互
 * @Author 张三金
 * @Date 2022/1/18 0018 21:48
 * @Company jzb
 * @Version 1.0.0
 */
public class Code13_Exchanger {

    static Exchanger<String> exchanger = new Exchanger<>();

    public static void main(String[] args) {
        new Thread(() -> {
            String name = "T1";

            try {
                // 等 3 秒钟，等不到别个来跟我交换，我就抛出超时异常
                name = exchanger.exchange(name, 3, TimeUnit.SECONDS);
                System.out.println("T1 拿到了 " + name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }


        }).start();

        new Thread(() -> {
            String name = "T2";
            try {
                name = exchanger.exchange(name);

                System.out.println("T2 拿到了 " + name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
