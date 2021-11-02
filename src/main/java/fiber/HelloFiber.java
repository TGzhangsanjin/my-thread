package fiber;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;

/**
 *
 * 失败了，，没看出线程的效果更好
 * @Author 张三金
 * @Date 2021/11/2 0002 13:57
 * @Company jzb
 * @Version 1.0.0
 */
public class HelloFiber {

    public static void main(String[] args) throws Exception{

        // 有一个错误提示貌似是需要添加一个启动参数  -javaagent:E:\repository\co\paralleluniverse\quasar-core\0.7.4\quasar-core-0.7.4.jar

        int size = 5_000_000;
        // 5ms 左右
        noThread(size);
        // 10s左右
        myThread(size);
        // 23s左右
        userFiber(size);
    }

    static void userFiber (int size) throws Exception{
        long start = System.currentTimeMillis();
        Fiber<Void>[] fibers = new Fiber[size];
        for (int i = 0; i < fibers.length; i++) {
            fibers[i] = new Fiber<Void>(new SuspendableRunnable() {
                @Override
                public void run() throws SuspendExecution, InterruptedException {
                    calculate();
                }
            });
        }
        for (int i = 0; i < fibers.length; i++) {
            fibers[i].start();
        }
        for (int i = 0; i < fibers.length; i++) {
            fibers[i].join();
        }
        long end = System.currentTimeMillis();
        System.out.println("开启 " + size + "个纤程，计算耗时： " + (end - start) + "ms");
    }

    static void noThread (int size){
        long start = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            calculate();
        }
        long end = System.currentTimeMillis();
        System.out.println("单个进程，计算耗时： " + (end - start) + "ms");
    }

    static void myThread (int size) throws Exception{
        long start = System.currentTimeMillis();
        Runnable runnable = HelloFiber::calculate;
        Thread[] threads = new Thread[size];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(runnable);
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        long end = System.currentTimeMillis();
        System.out.println("开启 " + size + "个线程，计算耗时： " + (end - start) + "ms");
    }

    /**
     * 啥也不干就是用来耗时间用的
     */
    static void calculate () {
        int result = 0;
        for (int i= 0; i < 10000; i ++) {
            for (int j = 0; j < 200; j++) {

            }
        }
    }
}
